package org.sphix

trait FirableVal[A] extends Val[A] with ValImpl[A] {

  protected def currentValue: A
  
  def fire() {
    invalidationListeners foreach (_ invalidated this)
    changeListeners foreach (_ changed (this, currentValue, apply()))    
  }
}