package org.sphix.collection.transformation

import scala.jdk.CollectionConverters._

import javafx.beans.value.ObservableValue
import javafx.beans.binding.ObjectBinding
import javafx.beans.InvalidationListener
import javafx.beans.Observable
import javafx.collections.FXCollections
import org.sphix.collection.ObservableSeq
import org.sphix.collection.Change
import javafx.collections.{ ListChangeListener, ObservableList }

//  The JavaFX FilteredList currently has a bug that makes it unusable
//  https://bugs.openjdk.java.net/browse/JDK-8195750
//  So we supply our own naive implementation of a filtered list as a workaround

class TempFilteredSeq[A](source: ObservableSeq[A], predicate: ObservableValue[A => Boolean])
  extends ObservableSeq[A] {

  def this(source: ObservableSeq[A], predicate: A => Boolean) =
    this(source, new ObjectBinding[A => Boolean] { def computeValue = predicate })

  protected val observableList = FXCollections.observableArrayList[A]

  // def originalSource: ObservableSeq[A] = source match {
  //   case fs: TempFilteredSeq[A] => fs.originalSource
  //   case os: ObservableSeq[A] => os
  // }

  // //	Use this wrapper instead of the much built-in FXCollections.unmodifiableObservableList wrapper
  // //	since we want to support a sort() method that delegates back to the original source.
  // def toObservableList = new UnmodifiableObservableListWrapper[A] with com.sun.javafx.collections.SortableList[A] {

  //   def get(i: Int) = observableList get i
  //   def size = observableList.size
  //   def addListener(listener: InvalidationListener) = observableList addListener listener
  //   def removeListener(listener: InvalidationListener) = observableList addListener listener
  //   def addListener(listener: ListChangeListener[_ >: A]) = observableList addListener listener
  //   def removeListener(listener: ListChangeListener[_ >: A]) = observableList addListener listener
  //   def sort() = { throw new UnsupportedOperationException("Please provide a java.util.Comparator.") }
  //   override def sort(comparator: java.util.Comparator[_ >: A]) = { FXCollections.sort[A](originalSource.toObservableList, comparator) }
  // }

  def toObservableList = FXCollections.unmodifiableObservableList(observableList)

  def refilter() = {
    //	val filtered: Seq[A] = source filter predicate.getValue		//	weird behaviour
    val p = predicate.getValue
    val filtered = source filter p
    observableList setAll filtered.asJava
  }

  predicate addListener new InvalidationListener {
    def invalidated(o: Observable) = {
      refilter()
    }
  }

  source onChange {
    _ foreach {
      case Change.Added(start, added) =>
        refilter() //	take the easy road
      case Change.Removed(start, removed) =>
        refilter() //	take the easy road
      case Change.Permutated(start, end, f) =>
        refilter() //	take the easy road

      case Change.Updated(start, end) =>
        refilter()
    }
  }

  refilter()
}

object TempFilteredSeq {
  implicit def toObservableList[A](fs: TempFilteredSeq[A]): ObservableList[A] = fs.toObservableList
}