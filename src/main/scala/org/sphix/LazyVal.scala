package org.sphix

import javafx.beans.InvalidationListener

trait LazyVal[A] extends FirableVal[A] {

  protected var value: A = _

  protected var valid = false

  protected def currentValue = value
  
  protected def invalidate(o: javafx.beans.Observable) = {
    valid = false
    fire()
  }

  protected lazy val lazyListener = new InvalidationListener {
    def invalidated(o: javafx.beans.Observable) = {
      invalidate(o)
    }
  }

  def getValue() = if (valid) value else {
    value = compute
    valid = true
    value
  }

  protected def compute: A
}