package org.sphix.collection

import javafx.beans.InvalidationListener
import javafx.collections.ListChangeListener
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import scala.collection.JavaConversions
import scala.collection.mutable.ArrayBuffer
import org.sphix.Observable
import org.sphix.collection.transformation._
import scala.collection.generic.SeqFactory
import scala.collection.generic.GenericTraversableTemplate
import scala.collection.generic.GenericCompanion

trait ObservableSeq[A]
  extends collection.Seq[A]
  with GenericTraversableTemplate[A, ObservableSeq]
  with Observable {

  override def companion: GenericCompanion[ObservableSeq] = ObservableSeq

  //	Backing list

  protected val observableList: ObservableList[A]

  //	Seq implementations

  def apply(n: Int) = observableList get n

  def iterator = JavaConversions asScalaIterator observableList.iterator

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
            seqChanges += Change.Added(change.getFrom, (JavaConversions asScalaBuffer change.getAddedSubList))
          }
          if (change.wasRemoved) {
            seqChanges += Change.Removed(change.getFrom, (JavaConversions asScalaBuffer change.getRemoved))
          }
          if (change.wasPermutated) {
            seqChanges += Change.Permutated(change.getFrom, change.getTo, change.getPermutation)
          }
          if (change.wasUpdated) {
            seqChanges += Change.Updated(change.getFrom, change.getTo)
          }
        }
        f(seqChanges)
      }
    }
    observableList addListener listener
    new ListChangeObserver(Seq(observableList), listener)
  }

  def onAdded[U](f: Seq[A] => U) = onChange { changes =>
    changes collect { case c: Change.Added[A] => c } foreach (c => f(c.added))
  }

  def onRemoved[U](f: Seq[A] => U) = onChange { changes =>
    changes collect { case c: Change.Removed[A] => c } foreach (c => f(c.removed))
  }
  //	Transformations

  def filtered(f: A => Boolean) = new FilteredSeq(this, f)
  def filtered(f: ObservableValue[A => Boolean]) = new FilteredSeq(this, f)

  //	Conversion

  def toObservableList: ObservableList[A]
}

object ObservableSeq extends SeqFactory[ObservableSeq] {

  def newBuilder[A] = immutable.ObservableSeq.newBuilder[A]

  //  def apply[A](elementChange: A => Observable) = immutable.ObservableSeq[A](elementChange)

  implicit def cbf[A] = new GenericCanBuildFrom[A] {
    override def apply() = newBuilder[A]
  }

  implicit def toObservableList[A](os: ObservableSeq[A]) = os.toObservableList
}