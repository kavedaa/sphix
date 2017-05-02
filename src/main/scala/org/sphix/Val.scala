package org.sphix

import scala.collection.mutable.ListBuffer
import javafx.beans.InvalidationListener
import javafx.beans.value.ChangeListener
import javafx.beans.value.{ ObservableValue => OV }
import java.util.concurrent.TimeUnit

abstract class Val[A] extends Observable with OV[A] { v =>

  def apply() = getValue

  def onChange[U](f: (OV[_ <: A], A, A) => U): ChangeObserver[A, U] = {
    val observer = new ChangeObserver[A, U](Seq(this), f)
    addListener(observer)
    observer
  }

  def onChangeOnce[U](f: (OV[_ <: A], A, A) => U): Observer = {
    val observer = new ChangeObserverLike[A](Seq(this)) {
      def changed(ov: OV[_ <: A], oldValue: A, newValue: A) {
        f(ov, oldValue, newValue)
        removeListener(this)
      }
    }
    addListener(observer)
    observer
  }

  def onValue[U](f: A => U) = onChange { (ov, oldValue, newValue) => f(newValue) }

  def onValueOnce[U](f: A => U) = onChangeOnce { (ov, oldValue, newValue) => f(newValue) }

  def on[U](value: A)(f: => U) = onValue { v => if (v == value) f }

  def onOnce[U](value: A)(f: => U) = onValueOnce { v => if (v == value) f }

  def map[B](f: A => B): Func[B] = new Func1(this, f)

  def flatten[B](implicit ev: A <:< OV[B]): Val[B] = new Flattener[B](this map ev)

  def filter(p: A => Boolean): Func[Option[A]] = new Func[Option[A]](this) {
    def compute = if (p(v())) Some(v()) else None
  }

  def delay(millis: Long) = new DelayedVal(this, millis, TimeUnit.MILLISECONDS)

  def as[B](implicit f: A => B) = map[B](f)

  //  def ! = this
  //  
  //  def unary_- = this
  //  
  //  def jfx = this
  //  
  //  def safe = this
  //  
  //  def ~ = this
  //  
  override def toString = s"Val(${getValue})"
}

trait ValImpl[A] extends ObservableImpl {

  protected lazy val changeListeners = ListBuffer[ChangeListener[_ >: A]]()

  def addListener(listener: ChangeListener[_ >: A]) {
    changeListeners += listener
  }

  def removeListener(listener: ChangeListener[_ >: A]) {
    changeListeners -= listener
  }
}

trait ValProxy[A] extends Val[A] with ObservableProxy {
  val jfx: OV[A]
  def getValue = jfx.getValue
  def addListener(listener: ChangeListener[_ >: A]) { jfx addListener listener }
  def removeListener(listener: ChangeListener[_ >: A]) { jfx removeListener listener }

}

object Val {

  def apply[A](value: A): Val[A] = new ConstantVal(value)

  def apply[A](value: A, f: A => javafx.beans.Observable) = new ObservingVal(value, f)

  //	Not a good idea as it gets triggered way too easily given the apply method on Val...  
  //  implicit def fromValue[A](v: A): Val[A] = Val(v) 

  //	do this instead

  //	please note that there are is some unintended wordplay here...
  implicit class Extender[A](val a: A) extends AnyVal {
    def toVal: Val[A] = new ConstantVal(a)
    def observing(f: A => javafx.beans.Observable): Val[A] = new ObservingVal(a, f)
  }

  implicit class JfxObservableValuePimper[A](val jfx: OV[A]) extends ValProxy[A]

  implicit class OVTuple2[A1, A2](val t: (OV[A1], OV[A2])) extends TupleObservable {

    def onValue[U](f: (A1, A2) => U) = onInvalidate(o => f(t._1.getValue, t._2.getValue))

    def onValueOnce[U](f: (A1, A2) => U) = onInvalidateOnce(o => f(t._1.getValue, t._2.getValue))

    def map[B](f: (A1, A2) => B): Func[B] = new Func[B](t._1, t._2) {
      def compute = f(t._1(), t._2())
    }

    //	not sure if this is actually useful for anything
    def pack = map((_, _))
  }

  implicit class OVTuple3[A1, A2, A3](val t: (OV[A1], OV[A2], OV[A3])) extends TupleObservable {

    def onValue[U](f: (A1, A2, A3) => U) = onInvalidate(o => f(t._1.getValue, t._2.getValue, t._3.getValue))

    def onValueOnce[U](f: (A1, A2, A3) => U) = onInvalidateOnce(o => f(t._1.getValue, t._2.getValue, t._3.getValue))

    def map[B](f: (A1, A2, A3) => B): Func[B] = new Func[B](t._1, t._2, t._3) {
      def compute = f(t._1(), t._2(), t._3())
    }

    def pack = map((_, _, _))
  }

  implicit class OVTuple4[A1, A2, A3, A4](val t: (OV[A1], OV[A2], OV[A3], OV[A4])) {
    def map[B](f: (A1, A2, A3, A4) => B): Func[B] = new Func[B](t._1, t._2, t._3, t._4) {
      def compute = f(t._1(), t._2(), t._3(), t._4())
    }
  }

  implicit class OVTuple5[A1, A2, A3, A4, A5](val t: (OV[A1], OV[A2], OV[A3], OV[A4], OV[A5])) {
    def map[B](f: (A1, A2, A3, A4, A5) => B): Func[B] = new Func[B](t._1, t._2, t._3, t._4, t._5) {
      def compute = f(t._1(), t._2(), t._3(), t._4(), t._5())
    }
  }

  implicit class OVTuple6[A1, A2, A3, A4, A5, A6](val t: (OV[A1], OV[A2], OV[A3], OV[A4], OV[A5], OV[A6])) {
    def map[B](f: (A1, A2, A3, A4, A5, A6) => B): Func[B] = new Func[B](t._1, t._2, t._3, t._4, t._5, t._6) {
      def compute = f(t._1(), t._2(), t._3(), t._4(), t._5(), t._6())
    }
  }

  implicit class OVTuple7[A1, A2, A3, A4, A5, A6, A7](val t: (OV[A1], OV[A2], OV[A3], OV[A4], OV[A5], OV[A6], OV[A7])) {
    def map[B](f: (A1, A2, A3, A4, A5, A6, A7) => B): Func[B] = new Func[B](t._1, t._2, t._3, t._4, t._5, t._6, t._7) {
      def compute = f(t._1(), t._2(), t._3(), t._4(), t._5(), t._6(), t._7())
    }
  }

  implicit class OVTuple8[A1, A2, A3, A4, A5, A6, A7, A8](val t: (OV[A1], OV[A2], OV[A3], OV[A4], OV[A5], OV[A6], OV[A7], OV[A8])) {
    def map[B](f: (A1, A2, A3, A4, A5, A6, A7, A8) => B): Func[B] = new Func[B](t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8) {
      def compute = f(t._1(), t._2(), t._3(), t._4(), t._5(), t._6(), t._7(), t._8())
    }
  }

  implicit class OVTuple9[A1, A2, A3, A4, A5, A6, A7, A8, A9](val t: (OV[A1], OV[A2], OV[A3], OV[A4], OV[A5], OV[A6], OV[A7], OV[A8], OV[A9])) {
    def map[B](f: (A1, A2, A3, A4, A5, A6, A7, A8, A9) => B): Func[B] = new Func[B](t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9) {
      def compute = f(t._1(), t._2(), t._3(), t._4(), t._5(), t._6(), t._7(), t._8(), t._9())
    }
  }

  implicit class OVTuple10[A1, A2, A3, A4, A5, A6, A7, A8, A9, A10](val t: (OV[A1], OV[A2], OV[A3], OV[A4], OV[A5], OV[A6], OV[A7], OV[A8], OV[A9], OV[A10])) {
    def map[B](f: (A1, A2, A3, A4, A5, A6, A7, A8, A9, A10) => B): Func[B] = new Func[B](t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9, t._10) {
      def compute = f(t._1(), t._2(), t._3(), t._4(), t._5(), t._6(), t._7(), t._8(), t._9(), t._10())
    }
  }

  //	And so on...
}

