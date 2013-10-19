package org.sphix.collection

import javafx.collections.ObservableList
import javafx.collections.ListChangeListener
import org.sphix.Observer

sealed trait Change[+A]

object Change {
  case class Added[A](start: Int, added: Seq[A]) extends Change[A]
  case class Removed[A](start: Int, removed: Seq[A]) extends Change[A]
  case class Permutated(start: Int, end: Int, permutation: (Int => Int)) extends Change[Nothing]
  case class Updated(start: Int, end: Int) extends Change[Nothing]
}

class ListChangeObserver[A](observableLists: Seq[ObservableList[A]], listener: ListChangeListener[A])
  extends Observer {
  def dispose() {
    observableLists foreach { _ removeListener listener }
  }
}