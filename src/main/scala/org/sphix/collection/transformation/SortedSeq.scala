package org.sphix.collection.transformation

import javafx.beans.value.ObservableValue
import javafx.collections.transformation.SortedList

import org.sphix.collection.ObservableSeq

class SortedSeq[A](source: ObservableSeq[A])
  extends ObservableSeq[A] {

  protected val observableList = new SortedList(source)

  def setComparator(comparator: ObservableValue[java.util.Comparator[A]]) = {
    observableList.comparatorProperty.unbind()
    observableList.comparatorProperty bind comparator
  }

  def toObservableList = observableList
}