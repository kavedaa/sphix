package org.sphix

import scala.collection.mutable.ListBuffer
import javafx.beans.{ property => jfxbp }
import javafx.beans.{ value => jfxbv }
import javafx.beans.value.ChangeListener
import org.sphix.util.FullConverter
import scala.annotation.unchecked.uncheckedVariance

abstract class Var[A]
  extends Val[A] with jfxbp.Property[A] {

  def update(newValue: A) = setValue(newValue)

  def <==(ov: javafx.beans.value.ObservableValue[A]) = bind(ov)

  def <~= = ???

  def <-=(ov: javafx.beans.value.ObservableValue[Option[A]]) =
    bind(Func(ov, this)((ov, th) => ov.getValue getOrElse th()))

  def <==>(that: jfxbp.Property[A]) = bindBidirectional(that)

  def <=~=>[B](that: jfxbp.Property[B])(implicit converter: FullConverter[A, B]) = 
    bindBidirectionalWithConverter(that, converter)
  
  protected case class BidirBinding[+B](property: jfxbp.Property[B @uncheckedVariance], converter: FullConverter[A, B @uncheckedVariance], listener: ChangeListener[B @uncheckedVariance])

  protected lazy val bidirBoundTo = ListBuffer[BidirBinding[Any]]()

  protected var updating = false

  def bindBidirectionalWithConverter[B](that: jfxbp.Property[B], converter: FullConverter[A, B]): Unit = {
    val listener = new ChangeListener[B] {
      def changed(ov: jfxbv.ObservableValue[_ <: B], oldVal: B, newVal: B) = {
        setValue(converter deconvert newVal)
      }
    }
    bidirBoundTo += BidirBinding(that, converter, listener)
    that addListener listener
    updating = true
    setValue(converter deconvert that.getValue)
    updating = false
  }

  def unbindBidirectionalAny(that: jfxbp.Property[_]): Unit = {
    bidirBoundTo find (_.property == that) foreach { binding =>
      that removeListener binding.listener
      bidirBoundTo -= binding
    }
  }
  
  def <=!=>(that: jfxbp.Property[_]): Unit = unbindBidirectionalAny(that)
  
  def <=~[B](holder: Var.ConverterHolder[A, B]) = bindBidirectionalWithConverter(holder.that, holder.converter)

  //  def <-~= = ???
  //  
  //  def <|== = ???
  //
  //    def <:==(that: Val[A])
  //
  //    def :=(that: A)
  
  def convert[B](implicit converter: FullConverter[A, B]): Var[B] = new ConvertedVar(this, converter)

  override def toString = s"Var(${getValue})"
}

// trait WritableVal[A] extends LazyVal

trait VarImpl[A] extends Var[A] with LazyVal[A] {

  protected def write(newValue: A) = {
    if (newValue != value) {
      val oldValue = value
      value = newValue
      if (!updating) {
        updating = true
        bidirBoundTo foreach (binding => binding.property setValue (binding.converter convert newValue))
        updating = false
      }
      invalidationListeners.toSeq foreach (_ invalidated this)
      changeListeners.toSeq foreach (_ changed (this, oldValue, value))
    }
  }

  def setValue(newValue: A) = {
    if (!isBound) write(newValue)
    else throw new RuntimeException("A bound value cannot be set.")
  }

  private var boundTo: Option[jfxbv.ObservableValue[_ <: A]] = None

  def isBound = boundTo.isDefined

  def bind(that: jfxbv.ObservableValue[_ <: A]) = {
    if (isBound) unbind()
    boundTo = Some(that)
    that addListener lazyListener
    invalidate(that)
  }

  def unbind() = {
    boundTo map { ov => ov removeListener lazyListener }
    boundTo = None
  }

  def bindBidirectional(that: jfxbp.Property[A]): Unit = {
    bidirBoundTo += BidirBinding(that, FullConverter.identity, bidirListener)
    that addListener bidirListener
    setValue(that.getValue) //	might be possible to achieve laziness here
  }

  protected lazy val bidirListener = new ChangeListener[A] {
    def changed(ov: jfxbv.ObservableValue[_ <: A], oldVal: A, newVal: A) = {
      setValue(newVal)
    }
  }

  def unbindBidirectional(that: jfxbp.Property[A]): Unit = 
    unbindBidirectionalAny(that)

  protected def compute = boundTo map (_.getValue) getOrElse value
}

trait VarProxy[A] extends Var[A] with ValProxy[A] {
  val jfx: jfxbp.Property[A]
  def getName = jfx.getName
  def getBean = jfx.getBean
  def setValue(v: A) = jfx setValue v
  def bind(that: jfxbv.ObservableValue[_ <: A]) = jfx bind that
  def unbind() = jfx.unbind()
  def isBound = jfx.isBound
  def bindBidirectional(other: jfxbp.Property[A]) = jfx bindBidirectional other
  def unbindBidirectional(other: jfxbp.Property[A]) = jfx unbindBidirectional other
}

object Var {

  def apply[A](initValue: A = null): Var[A] = new SimpleVar[A](initValue, "", null)

  def apply[A](initValue: A, name: String): Var[A] = new SimpleVar[A](initValue, name, null)

  def apply[A](initValue: A, name: String, bean: Object): Var[A] = new SimpleVar[A](initValue, name, bean)

  implicit class JfxPropertyPimper[A](val jfx: jfxbp.Property[A]) extends VarProxy[A]

  case class ConverterHolder[A, B](converter: FullConverter[A, B], that: jfxbp.Property[B])

  implicit class FullConverterExt[A, B](converter: FullConverter[A, B]) {
    def ~=>(that: jfxbp.Property[B]) = new ConverterHolder(converter, that)
  }
}