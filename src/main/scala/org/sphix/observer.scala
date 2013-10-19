package org.sphix

import javafx.beans.InvalidationListener
import javafx.beans.value.ChangeListener
import javafx.{ beans => jfxb }
import jfxb.{ value => jfxbv }

trait Observer {
  def dispose()
}

class InvalidationObserver(observables: Seq[jfxb.Observable], listener: InvalidationListener)
  extends Observer {
  def dispose() {
    observables foreach { _ removeListener listener }
  }
}

class ChangeObserver[A](observableValues: Seq[jfxbv.ObservableValue[A]], listener: ChangeListener[A])
  extends Observer {
  def dispose() {
    observableValues foreach { _ removeListener listener }
  }
}