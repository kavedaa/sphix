package org.sphix

import javafx.beans.{ property => jfxbp }
import javafx.beans.{ value => jfxbv }
import org.sphix.util.FullConverter
import javafx.beans.value.ChangeListener

class ConvertedVar[A, B](remote: jfxbp.Property[B], converter: FullConverter[B, A])
  extends Var[A] with LazyVal[A] {

  def getName = remote.getName
  def getBean = remote.getBean

  remote addListener lazyListener

  override def invalidate(o: javafx.beans.Observable) {
    super.invalidate(o)
    if (o != remote) remote setValue (converter deconvert compute)
  }

  protected def write(a: A) {
    if (a != value) {
      val oldValue = value
      value = a
      remote setValue (converter deconvert a)
      if (!updating) {
        updating = true
        bidirBoundTo foreach (binding => binding.property setValue (binding.converter convert a))
        updating = false
      }
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
    invalidate(that)
  }

  def unbind() {
    boundTo map { ov => ov removeListener lazyListener }
    boundTo = None
  }

  def bindBidirectional(that: jfxbp.Property[A]): Unit = {
    bidirBoundTo += BidirBinding(that, FullConverter.identity, bidirListener)
    that addListener bidirListener
    setValue(that.getValue) //	might be possible to achieve laziness here
  }

  protected lazy val bidirListener = new ChangeListener[A] {
    def changed(ov: jfxbv.ObservableValue[_ <: A], oldVal: A, newVal: A) {
      setValue(newVal)
    }
  }

  def unbindBidirectional(that: jfxbp.Property[A]): Unit =
    unbindBidirectionalAny(that)

  def getRemote = converter convert remote.getValue

  protected def compute = boundTo map (_.getValue) getOrElse getRemote
}

