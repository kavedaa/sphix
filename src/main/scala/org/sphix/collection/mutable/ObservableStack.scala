package org.sphix.collection.mutable

import javafx.collections.ObservableList
import scala.collection.mutable._
import scala.collection.generic._
import javafx.collections.FXCollections
import scala.collection.JavaConversions

class ObservableStack[A] private (protected val observableList: ObservableList[A])
  extends org.sphix.collection.ObservableSeq[A]
  with Seq[A]
  with SeqLike[A, ObservableStack[A]]
  with GenericTraversableTemplate[A, ObservableStack] {

  override def companion = ObservableStack

  def update(n: Int, newElem: A) {
    observableList set (n, newElem)
  }

  def push(elem: A): this.type = {
    observableList add (0, elem)
    this
  }

  def pushAll(xs: TraversableOnce[A]): this.type = {
    observableList addAll (0, JavaConversions asJavaCollection xs.toSeq.reverse)
    this
  }

  def push(elem1: A, elem2: A, elems: A*): this.type = pushAll(elem1 +: elem2 +: elems)

  def top: A = head

  def pop(): A = {
    val res = head
    observableList remove 0
    res
  }

  def toObservableList = observableList
}

object ObservableStack extends SeqFactory[ObservableStack] {

  class StackBuilder[A] extends Builder[A, ObservableStack[A]] {
    val ob = new ObservableBuffer[A](FXCollections.observableArrayList[A])
    def +=(elem: A) = { ob += elem; this }
    def clear() = ob clear ()
    def result = new ObservableStack(ob.toObservableList)
  }

  def newBuilder[A] = new StackBuilder

  implicit def cbf[A] = new GenericCanBuildFrom[A] {
    override def apply() = newBuilder[A]
  }

  implicit def toObservableList[A](ob: ObservableBuffer[A]) = ob.toObservableList

  implicit def fromObservableList[A](ol: ObservableList[A]) = new ObservableStack(ol)
}