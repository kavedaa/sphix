package org.sphix.collection

import scala.collection.SeqFactory
import scala.collection.mutable.{ Builder, ArrayBuffer }
import scala.jdk.CollectionConverters._

import javafx.beans.InvalidationListener
import javafx.beans.value.ObservableValue

import javafx.collections._

import org.sphix.Observable
import org.sphix.collection.transformation._

trait ObservableSeq[A]
  extends scala.collection.Seq[A] 
  with scala.collection.SeqOps[A, ObservableSeq, ObservableSeq[A]]
  with Observable {

  override val iterableFactory: SeqFactory[ObservableSeq] = ObservableSeq
  override protected def fromSpecific(coll: IterableOnce[A]) = iterableFactory.from(coll)
  override protected def newSpecificBuilder: Builder[A, ObservableSeq[A]] = iterableFactory.newBuilder
  override def empty = iterableFactory.empty[A]

  //	Backing list

  protected val observableList: ObservableList[A]

  //	Seq implementations

  def apply(n: Int) = observableList get n

  def iterator = observableList.iterator.asScala

  def length = observableList.size

  //	Observable implementations

  def addListener(listener: InvalidationListener) {
    observableList addListener listener
  }

  def removeListener(listener: InvalidationListener) {
    observableList removeListener listener
  }

  //	ObservableSeq stuff

  def onChange[U](f: Seq[Change[A]] => U) = {
    val listener = new ListChangeListener[A] {
      def onChanged(change: javafx.collections.ListChangeListener.Change[_ <: A]) {
        val seqChanges = new ArrayBuffer[Change[A]]()
        while (change.next()) {
          if (change.wasAdded) {
            seqChanges += Change.Added(change.getFrom, change.getAddedSubList.asScala)
          }
          if (change.wasRemoved) {
            seqChanges += Change.Removed(change.getFrom, change.getRemoved.asScala)
          }
          if (change.wasPermutated) {
            seqChanges += Change.Permutated(change.getFrom, change.getTo, change.getPermutation)
          }
          if (change.wasUpdated) {
            seqChanges += Change.Updated(change.getFrom, change.getTo)
          }
        }
        f(seqChanges.toSeq)
      }
    }
    observableList addListener listener
    new ListChangeObserver(Seq(observableList), listener)
  }

  def onAdded[U](f: Iterable[A] => U) = onChange { changes =>
    changes collect { case c: Change.Added[A] => c } foreach (c => f(c.added))
  }

  def onRemoved[U](f: Iterable[A] => U) = onChange { changes =>
    changes collect { case c: Change.Removed[A] => c } foreach (c => f(c.removed))
  }

  //	Transformations

  def filtered(f: A => Boolean) = new TempFilteredSeq(this, f)
  def filtered(f: ObservableValue[A => Boolean]) = new TempFilteredSeq(this, f)

  //	Conversion

  def toObservableList: javafx.collections.ObservableList[A]
}

object ObservableSeq extends SeqFactory[ObservableSeq] {

  def empty[A]: ObservableSeq[A] = org.sphix.collection.immutable.ObservableSeq.empty[A]

  def from[A](source: IterableOnce[A]): ObservableSeq[A] = org.sphix.collection.immutable.ObservableSeq.from(source)

  def newBuilder[A] = org.sphix.collection.immutable.ObservableSeq.newBuilder[A]

  implicit def toObservableList[A](os: ObservableSeq[A]) = os.toObservableList

  implicit def fromObservableList[A](ol: ObservableList[A]) = org.sphix.collection.immutable.ObservableSeq.fromObservableList(ol)

  def from[A](xs: Seq[A]) = fromObservableList(FXCollections.observableList(xs.asJava))

  //  scala.Seq is now the immutable Seq, therefore we need an implicit conversion
  implicit def toSeq[A](os: ObservableSeq[A]): Seq[A] = os.toSeq

  //  this is for going directly Seq -> ObservableList
  //  just add wrappers instead of creating a new collection
  //  (has nothing to do with ObservableSeq, just convenient to place it here)
  implicit def seqAsJavaObservableList[A](xs: Seq[A]): ObservableList[A] =
    FXCollections.observableList(xs.asJava)
}