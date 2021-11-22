package org.sphix

trait FirableVal[A] extends Val[A] with ValImpl[A] {

  protected def currentValue: A

//  private var isIgnoring = false

  private var isAtomic = false
  private var invalidated = false
  
  def fire() {
    if (!isAtomic) {
      invalidationListeners.toSeq foreach (_ invalidated this)
      changeListeners.toSeq foreach (_ changed (this, currentValue, getValue))
    } 
    else {
      invalidated = true
    }
  }
  
//  def ignore[U](b: => U) = {
//    isIgnoring = true
//    b
//    isIgnoring = false
//  }
  
  def atomic[U](proc: => U) = {
    isAtomic = true
    proc
    isAtomic = false
    if (invalidated) fire()
  }
}