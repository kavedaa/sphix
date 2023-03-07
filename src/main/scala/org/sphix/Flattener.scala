package org.sphix

import javafx.beans.value.ObservableValue
import javafx.beans.InvalidationListener

class Flattener[A](outer: ObservableValue[ObservableValue[A]]) extends LazyVal[A] {

  private var inner = outer.getValue

  inner addListener lazyListener

  protected lazy val outerListener = new InvalidationListener {
    def invalidated(o: javafx.beans.Observable) = {
      inner removeListener lazyListener
      //	this could probably be optimized by not reading outer before at computation, e.g. proper laziness
      inner = outer.getValue
      inner addListener lazyListener
      invalidate(outer)
    }
  }

  outer addListener outerListener

  def compute = inner.getValue
}