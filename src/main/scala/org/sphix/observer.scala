package org.sphix

import javafx.beans.InvalidationListener
import javafx.beans.value.ChangeListener
import javafx.{ beans => jfxb }
import javafx.beans.value.{ ObservableValue => OV }

trait Observer {

  def dispose(): Unit

  protected var isIgnoring = false

  def ignore[U](b: => U) = {
    isIgnoring = true
    b
    isIgnoring = false
  }
}

abstract class InvalidationObserverLike(observables: Seq[jfxb.Observable])
  extends InvalidationListener with Observer {

  def dispose() = {
    observables foreach { _ removeListener this }
  }

}

class InvalidationObserver[U](observables: Seq[jfxb.Observable], f: jfxb.Observable => U)
  extends InvalidationObserverLike(observables) {

  def invalidated(o: jfxb.Observable) = {
    if (!isIgnoring) f(o)
  }
}

abstract class ChangeObserverLike[A](observableValues: Seq[OV[A]])
  extends ChangeListener[A] with Observer {

  def dispose() = {
    observableValues foreach { _ removeListener this }
  }
}

class ChangeObserver[A, U](observableValues: Seq[OV[A]], f: (OV[_ <: A], A, A) => U)
  extends ChangeObserverLike(observableValues) {

  def changed(ov: OV[_ <: A], oldValue: A, newValue: A) = {
    if (!isIgnoring) f(ov, oldValue, newValue)
  }

}