package org.sphix.collection.transformation

import java.util.function.Predicate

import javafx.beans.value.ObservableValue
import javafx.collections.transformation.FilteredList

import org.sphix.Val
import org.sphix.collection.ObservableSeq

class FilteredSeq[A](source: ObservableSeq[A], predicate: Val[A => Boolean])
  extends ObservableSeq[A] {

  def this(source: ObservableSeq[A], predicate: A => Boolean) =
    this(source, Val(predicate))

  protected val observableList = new FilteredList(source)
  
  val javaPredicate: Val[Predicate[A]] = predicate map { p =>
    (a: A) => p(a): java.lang.Boolean
  }

  observableList.predicateProperty bind javaPredicate
  
  def toObservableList = observableList
}
