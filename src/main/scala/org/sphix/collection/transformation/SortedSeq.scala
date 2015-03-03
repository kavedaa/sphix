package org.sphix.collection.transformation

import org.sphix.collection.ObservableSeq
import javafx.beans.value.ObservableValue
import javafx.collections.transformation.SortedList
import javafx.collections.ObservableList

class SortedSeq[A](source: ObservableSeq[A], comparator: ObservableValue[java.util.Comparator[A]])
  extends ObservableSeq[A] {

  protected val observableList = new SortedList(source)

  observableList.comparatorProperty bind comparator
  
  def toObservableList = observableList
}

object SortedSeq {
  implicit def toObservableList[A](ss: SortedSeq[A]) = ss.toObservableList
}