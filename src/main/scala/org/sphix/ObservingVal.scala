package org.sphix

import javafx.beans.InvalidationListener

class ObservingVal[A](value: A, f: A => javafx.beans.Observable)
  extends ConstantVal[A](value) with FirableVal[A] {

  def currentValue = value

  val listener = new InvalidationListener {
    def invalidated(o: javafx.beans.Observable) {
      fire()
    }
  }

  f(value) addListener listener
}