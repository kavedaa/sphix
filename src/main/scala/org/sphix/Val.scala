package org.sphix

import scala.collection.mutable.ListBuffer
import javafx.beans.InvalidationListener
import javafx.beans.value.ChangeListener
import javafx.beans.value.{ ObservableValue => OV }

abstract class Val[A] extends Observable with OV[A] { v =>

  def apply() = getValue

  def onChange[U](f: (OV[_ <: A], A, A) => U): Observer = {
    val listener = new ChangeListener[A] {
      def changed(ov: OV[_ <: A], oldValue: A, newValue: A) {
        f(ov, oldValue, newValue)
      }
    }
    addListener(listener)
    new ChangeObserver(Seq(this), listener)
  }

  def onChangeOnce[U](f: (OV[_ <: A], A, A) => U): Observer = {
    val listener = new ChangeListener[A] {
      def changed(ov: OV[_ <: A], oldValue: A, newValue: A) {
        f(ov, oldValue, newValue)
        removeListener(this)
      }
    }
    addListener(listener)
    new ChangeObserver(Seq(this), listener)
  }

  def onValue[U](f: A => U): Observer = onChange { (ov, oldValue, newValue) => f(newValue) }

  def onValueOnce[U](f: A => U): Observer = onChangeOnce { (ov, oldValue, newValue) => f(newValue) }

  def map[B](f: A => B): Func[B] = new Func[B](this) { def compute = f(v()) }

  def flatten[B](implicit ev: A <:< OV[B]): Val[B] = new Flattener[B](this map ev)

  def filter(p: A => Boolean): Func[Option[A]] = new Func[Option[A]](this) {
    def compute = if (p(v())) Some(v()) else None
  }

  def delay(millis: Long) = new DelayedVal(this, millis)

  def as[B](implicit f: A => B) = map[B](f)

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

  implicit class JfxObservableValuePimper[A](val jfx: OV[A]) extends ValProxy[A]

  implicit class ValTuple2[A1, A2](val t: (OV[A1], OV[A2])) extends TupleObservable {

    def onValue[U](f: (A1, A2) => U): Observer = onInvalidate(o => f(t._1.getValue, t._2.getValue))

    def onValueOnce[U](f: (A1, A2) => U): Observer = onInvalidateOnce(o => f(t._1.getValue, t._2.getValue))

    def map[B](f: (A1, A2) => B): Func[B] = new Func[B](t._1, t._2) {
      def compute = f(t._1(), t._2())
    }
  }

  implicit class ValTuple3[A1, A2, A3](val t: (Val[A1], Val[A2], Val[A3])) extends TupleObservable {
    
    def onValue[U](f: (A1, A2, A3) => U): Observer = onInvalidate(o => f(t._1.getValue, t._2.getValue, t._3.getValue))

    def onValueOnce[U](f: (A1, A2, A3) => U): Observer = onInvalidateOnce(o => f(t._1.getValue, t._2.getValue, t._3.getValue))
    
    def map[B](f: (A1, A2, A3) => B): Func[B] = new Func[B](t._1, t._2, t._3) {
      def compute = f(t._1(), t._2(), t._3())
    }
  }

  implicit class ValTuple4[A1, A2, A3, A4](val t: (Val[A1], Val[A2], Val[A3], Val[A4])) {
    def map[B](f: (A1, A2, A3, A4) => B): Func[B] = new Func[B](t._1, t._2, t._3, t._4) {
      def compute = f(t._1(), t._2(), t._3(), t._4())
    }
  }

  implicit class ValTuple5[A1, A2, A3, A4, A5](val t: (Val[A1], Val[A2], Val[A3], Val[A4], Val[A5])) {
    def map[B](f: (A1, A2, A3, A4, A5) => B): Func[B] = new Func[B](t._1, t._2, t._3, t._4, t._5) {
      def compute = f(t._1(), t._2(), t._3(), t._4(), t._5())
    }
  }

  //	And so on...
}

