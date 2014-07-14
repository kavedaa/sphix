package org.sphix.collection.mutable

import javafx.collections.ObservableList
import scala.collection.mutable.Buffer
import scala.collection.mutable.BufferLike
import scala.collection.mutable.Builder
import javafx.collections.FXCollections
import scala.collection.JavaConversions
import scala.collection.generic.SeqFactory
import scala.collection.generic.GenericTraversableTemplate
import org.sphix.collection.ObservableSeq
import org.sphix.Observer
import org.sphix.collection.Change._
import javafx.collections.ListChangeListener
import javafx.beans.Observable
import javafx.util.Callback

class ObservableBuffer[A](protected val observableList: ObservableList[A])
  extends org.sphix.collection.ObservableSeq[A]
  with Buffer[A]
  with BufferLike[A, ObservableBuffer[A]]
  with Builder[A, ObservableBuffer[A]]
  with GenericTraversableTemplate[A, ObservableBuffer] {

  override def companion = ObservableBuffer

  private var boundTo: Option[ObservableSeq[A]] = None

  val bindListener = new ListChangeListener[A] {
    def onChanged(change: javafx.collections.ListChangeListener.Change[_ <: A]) {
      println("observable buffer bind listener called")
      //	TODO: this is a temporary workaround for the horrible bugs in JFX
      observableList setAll change.getList

      //      while (change.next()) {
      //        if (change.wasAdded) {
      //          observableList addAll(change.getFrom, change.getAddedSubList)
      //        }
      //        if (change.wasRemoved) {
      //          //	workaround, possible bug i OList
      //          change.getFrom to change.getTo foreach { n =>
      //            observableList remove n
      //          }
      //        }
      //        if (change.wasPermutated) {
      //          //	pragmatic solution
      //          observableList setAll change.getList
      //        }
      //        if (change.wasReplaced) {
      //          // doesn't seem to be called
      //        }
      //        if (change.wasUpdated) {
      //          //	not sure if best solution...note that this prob won't be called anyway
      //          change.getFrom to change.getTo foreach { n =>
      //            observableList set (n, change.getList get n)
      //          }
      //        }
      //      }
    }
  }

  def <==(that: ObservableSeq[A]) {
    if (isBound) unbind()
    boundTo = Some(that)
    that addListener bindListener
    this setAll that
  }

  def unbind() {
    boundTo map { ob => ob removeListener bindListener }
    boundTo = None
  }

  def isBound = boundTo.isDefined

  def assertNotBound() = if (isBound) throw new RuntimeException("A bound buffer cannot be mutated.")

  def +=(elem: A) = {
    assertNotBound()
    observableList add elem
    this
  }

  def result = this

  def clear() {
    assertNotBound()
    observableList clear ()
  }

  override def newBuilder = ObservableBuffer.newBuilder

  def +=:(elem: A) = {
    assertNotBound()
    observableList add (0, elem)
    this
  }

  def insertAll(n: Int, elems: Traversable[A]) {
    assertNotBound()
    observableList addAll (n, JavaConversions asJavaCollection elems.toIterable)
  }

  def remove(n: Int) = {
    assertNotBound()
    observableList remove n
  }

  def update(n: Int, newElem: A) {
    assertNotBound()
    observableList set (n, newElem)
  }

  def update(elems: Traversable[A]) {
    assertNotBound()
    observableList setAll (JavaConversions asJavaCollection elems.toIterable)
  }

  //	Overrides to avoid element-for-element changes from default implementation

  override def ++=(xs: TraversableOnce[A]) = {
    assertNotBound()
    observableList addAll (JavaConversions asJavaCollection xs.toIterable)
    this
  }

  override def ++=:(xs: TraversableOnce[A]) = {
    assertNotBound()
    observableList addAll (0, JavaConversions asJavaCollection xs.toIterable)
    this
  }

  override def +=(elem1: A, elem2: A, elems: A*) = {
    assertNotBound()
    observableList addAll (JavaConversions asJavaCollection (Seq(elem1, elem2) ++ elems))
    this
  }

  override def --=(xs: TraversableOnce[A]) = {
    assertNotBound()
    observableList removeAll (JavaConversions asJavaCollection xs.toIterable)
    this
  }

  override def -=(elem1: A, elem2: A, elems: A*) = {
    assertNotBound()
    observableList removeAll (JavaConversions asJavaCollection (Seq(elem1, elem2) ++ elems))
    this
  }

  override def remove(n: Int, count: Int) {
    assertNotBound()
    observableList subList (n, n + count) clear ()
  }

  // NOTE: transform() cannot avoid this due to no corresponding method on OList
  // If this is deemed critical, either change implementation to use java.util.List as
  // backing...or file an issue against OList.

  def doSortBy[B](f: A => B)(implicit ord: math.Ordering[B]) {
    assertNotBound()
    FXCollections.sort[A](toObservableList, ord on f)
  }

  def doSortWith(lt: (A, A) => Boolean) {
    assertNotBound()
    FXCollections.sort[A](toObservableList, Ordering fromLessThan lt)
  }

  def toObservableList = observableList
}

object ObservableBuffer extends SeqFactory[ObservableBuffer] {

  def newBuilder[A] = new ObservableBuffer[A](FXCollections.observableArrayList[A])

  def apply[A](elementChange: A => Observable): ObservableBuffer[A] = {
    //	Not sure why JFX use array of Observable
    val callback = new Callback[A, Array[Observable]] {
      def call(a: A) = Array(elementChange(a))
    }
    new ObservableBuffer[A](FXCollections.observableArrayList[A](callback))
  }

  implicit def cbf[A] = new GenericCanBuildFrom[A] {
    override def apply() = newBuilder[A]
  }

  implicit def toObservableList[A](ob: ObservableBuffer[A]) = ob.toObservableList

  implicit def fromObservableList[A](ol: ObservableList[A]) = new ObservableBuffer(ol)
}