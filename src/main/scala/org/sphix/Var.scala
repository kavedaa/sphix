package org.sphix

import scala.collection.mutable.ListBuffer
import javafx.beans.{ property => jfxbp }
import javafx.beans.{ value => jfxbv }
import javafx.beans.value.ChangeListener

abstract class Var[A]
  extends Val[A] with jfxbp.Property[A] {

  def update(newValue: A) = setValue(newValue)

  def <==(ov: javafx.beans.value.ObservableValue[A]) = bind(ov)

  def <~= = ???

  def <-=(ov: javafx.beans.value.ObservableValue[Option[A]]) =
    bind(Func(ov, this)((ov, th) => ov.getValue getOrElse th()))

  def <-~= = ???

  override def toString = s"Var(${getValue})"
}

trait VarImpl[A] extends LazyVal[A] with ValImpl[A] {

  protected def write(newValue: A) {
    if (newValue != value) {
      val oldValue = value
      value = newValue
      invalidationListeners foreach (_ invalidated this)
      changeListeners foreach (_ changed (this, oldValue, value))
    }
  }

  def setValue(newValue: A) {
    if (!isBound) write(newValue)
    else throw new RuntimeException("A bound value cannot be set.")
  }
  
  private var boundTo: Option[jfxbv.ObservableValue[_ <: A]] = None

  def isBound = boundTo.isDefined

  def bind(that: jfxbv.ObservableValue[_ <: A]) {
    if (isBound) unbind()
    boundTo = Some(that)
    that addListener lazyListener
    invalidate()
  }

  def unbind() {
    boundTo map { ov => ov removeListener lazyListener }
    boundTo = None
  }

  private lazy val bidirBoundTo = ListBuffer[jfxbp.Property[A]]()

  protected lazy val bidirListener = new ChangeListener[A] {
    var updating = false
    def changed(ov: jfxbv.ObservableValue[_ <: A], oldVal: A, newVal: A) {
      if (!updating) {
        updating = true
        bidirBoundTo foreach (_ setValue newVal)
        updating = false
      }
    }
  }

  def bindBidirectional(other: jfxbp.Property[A]) = throw new UnsupportedOperationException

  def unbindBidirectional(other: jfxbp.Property[A]) = throw new UnsupportedOperationException

  protected def compute = boundTo map (_.getValue) getOrElse value  
}

trait VarProxy[A] extends Var[A] with ValProxy[A] {
  val jfx: jfxbp.Property[A]
  def getName = jfx.getName
  def getBean = jfx.getBean
  def setValue(v: A) = jfx setValue v
  def bind(that: jfxbv.ObservableValue[_ <: A]) = jfx bind that
  def unbind() = jfx unbind ()
  def isBound = jfx.isBound
  def bindBidirectional(other: jfxbp.Property[A]) = jfx bindBidirectional other
  def unbindBidirectional(other: jfxbp.Property[A]) = jfx unbindBidirectional other
}

object Var {

  def apply[A](initValue: A = null): Var[A] = new SimpleVar[A](initValue, "", null)

  def apply[A](initValue: A, name: String): Var[A] = new SimpleVar[A](initValue, name, null)

  def apply[A](initValue: A, name: String, bean: Object): Var[A] = new SimpleVar[A](initValue, name, bean)

  implicit class JfxPropertyPimper[A](val jfx: jfxbp.Property[A]) extends VarProxy[A]
}