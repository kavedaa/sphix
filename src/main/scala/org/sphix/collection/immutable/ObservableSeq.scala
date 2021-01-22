package org.sphix.collection.immutable

import scala.collection.SeqFactory
import scala.collection.mutable.Builder
import scala.jdk.CollectionConverters._

import javafx.collections._

class ObservableSeq[A](protected val observableList: ObservableList[A])
  extends org.sphix.collection.ObservableSeq[A]
  with scala.collection.immutable.Seq[A]
  with scala.collection.immutable.SeqOps[A, ObservableSeq, ObservableSeq[A]] {

  override val iterableFactory = ObservableSeq
  override protected def fromSpecific(coll: IterableOnce[A]) = iterableFactory.from(coll)
  override protected def newSpecificBuilder = iterableFactory.newBuilder
  override def empty = iterableFactory.empty[A]

  def toObservableList = FXCollections.unmodifiableObservableList(observableList)
}

object ObservableSeq extends SeqFactory[ObservableSeq] {

  private def javaList[A] = javafx.collections.FXCollections.observableArrayList[A]

  def empty[A]: ObservableSeq[A] = new ObservableSeq(javaList[A])
  
  def from[A](source: IterableOnce[A]): ObservableSeq[A] = {
    val list = javaList[A]
    source.iterator.asJava forEachRemaining { x => list add x }
    new ObservableSeq(list)
  }

  def newBuilder[A] = new Builder[A, ObservableSeq[A]] {

    private val list = javaList[A]

    def addOne(elem: A) = {
      list add elem
      this
    }

    def clear() = list.clear()

    def result = new ObservableSeq(list)
  }

  implicit def fromObservableList[A](ol: ObservableList[A]) = new ObservableSeq(ol)
}

