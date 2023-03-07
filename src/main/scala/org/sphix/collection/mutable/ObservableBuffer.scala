package org.sphix.collection.mutable

import javafx.beans.Observable
import javafx.util.Callback

import scala.collection.SeqFactory
import scala.collection.mutable.{ Buffer, Builder }
import scala.jdk.CollectionConverters._

import javafx.collections._

import org.sphix.Observer

class ObservableBuffer[A](protected val observableList: ObservableList[A])
  extends org.sphix.collection.ObservableSeq[A]
  with Buffer[A]
  with scala.collection.mutable.SeqOps[A, ObservableBuffer, ObservableBuffer[A]] {

  override val iterableFactory = ObservableBuffer
  override protected def fromSpecific(coll: IterableOnce[A]) = iterableFactory.from(coll)
  override protected def newSpecificBuilder = iterableFactory.newBuilder
  override def empty = iterableFactory.empty[A]

  //  Buffer implementations

  def addOne(elem: A) = {
    observableList add elem
    this
  }

  def clear() = {
    observableList.clear()
  }

  def insert(i: Int, elem: A) = {
    observableList.add(i, elem)
  }

  def insertAll(n: Int, elems: IterableOnce[A]) = {
    observableList.addAll(n, elems.toIterable.asJavaCollection)
  }

  def patchInPlace(from: Int, patch: IterableOnce[A], replaced: Int) = ???

  def prepend(elem: A) = {
    observableList add (0, elem)
    this
  }

  def remove(n: Int) = {
    observableList remove n
  }

  def remove(n: Int, count: Int) = {
    observableList.subList (n, n + count).clear()
  }

  def update(n: Int, elem: A) = {
    observableList set (n, elem)
  }

  def update(elems: IterableOnce[A]) = {
    observableList setAll elems.toIterable.asJavaCollection
  }
  
  //	Overrides to avoid element-for-element changes from default implementation

  override def addAll(xs: IterableOnce[A]) = {
    observableList addAll xs.toIterable.asJavaCollection
    this
  }

  override def prependAll(xs: IterableOnce[A]) = {
    observableList.addAll(0, xs.toIterable.asJavaCollection)
    this
  }

  override def subtractAll(xs: TraversableOnce[A]) = {
    observableList removeAll xs.toIterable.asJavaCollection
    this
  }

  //  Additional convenience methods

  /**
    *  Remove based on reference equality.
    */
  def removeRef(x: AnyRef) = {
    val index = indexWhere(_.asInstanceOf[AnyRef] eq x)
    if (index != -1) remove(index)
  }

  def toObservableList = observableList
}

object ObservableBuffer extends SeqFactory[ObservableBuffer] {

  private def javaList[A] = javafx.collections.FXCollections.observableArrayList[A]

  def empty[A]: ObservableBuffer[A] = new ObservableBuffer(javaList[A])
  
  def from[A](source: IterableOnce[A]): ObservableBuffer[A] = {
    val list = javaList[A]
    source.iterator.asJava forEachRemaining { x => list add x }
    new ObservableBuffer(list)
  }

  def newBuilder[A] = new Builder[A, ObservableBuffer[A]] {

    private val list = javaList[A]

    def addOne(elem: A) = {
      list add elem
      this
    }

    def clear() = list.clear()

    def result = new ObservableBuffer(list)
  }

  def apply[A](elementChange: A => Observable): ObservableBuffer[A] = {
    //	Not sure why JFX use array of Observable
    val callback = new Callback[A, Array[Observable]] {
      def call(a: A) = Array(elementChange(a))
    }
    new ObservableBuffer[A](FXCollections.observableArrayList[A](callback))
  }

  implicit def fromObservableList[A](ol: ObservableList[A]): ObservableBuffer[A] = new ObservableBuffer(ol)
}