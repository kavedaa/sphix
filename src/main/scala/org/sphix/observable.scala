package org.sphix

import scala.collection.mutable.ListBuffer
import javafx.beans.InvalidationListener
import javafx.beans.{ Observable => JFXO }

trait Observable extends JFXO {

  def onInvalidate[U](f: JFXO => U): InvalidationObserver[U] = {
    val observer = new InvalidationObserver(Seq(this), f)
    addListener(observer)
    observer
  }

  def onInvalidateOnce[U](f: JFXO => U): Observer = {
    val observer = new InvalidationObserverLike(Seq(this)) {
      def invalidated(o: JFXO) = { f(o); removeListener(this) }
    }
    addListener(observer)
    observer
  }

  def observe[U](f: => U) = onInvalidate((o: JFXO) => f)

  def observeOnce[U](f: => U) = onInvalidateOnce((o: JFXO) => f)

  def apply[A, B](f: this.type => A)(implicit applier: Applier[A, this.type, B]): B =
    applier apply (this, f)
}

trait ObservableImpl {

  protected lazy val invalidationListeners = ListBuffer[InvalidationListener]()

  def addListener(listener: InvalidationListener): Unit = {
    invalidationListeners += listener
  }

  def removeListener(listener: InvalidationListener): Unit = {
    invalidationListeners -= listener
  }
}

private[sphix] trait PluralObservable extends JFXO {

  def observables: Seq[JFXO]

  def addListener(listener: InvalidationListener) = {
    observables foreach { _ addListener listener }
  }

  def removeListener(listener: InvalidationListener) = {
    observables foreach { _ removeListener listener }
  }

  def onInvalidate[U](f: JFXO => U): InvalidationObserver[U] = {
    val observer = new InvalidationObserver(observables, f)
    addListener(observer)
    observer
  }

  def onInvalidateOnce[U](f: JFXO => U): Observer = {
    val observer = new InvalidationObserverLike(observables) {
      def invalidated(o: JFXO) = { f(o); removeListener(this) }
    }
    addListener(observer)
    observer
  }

  def observe[U](f: => U) = onInvalidate((o: JFXO) => f)

  def observeOnce[U](f: => U) = onInvalidateOnce((o: JFXO) => f)
}

private[sphix] trait TupleObservable extends PluralObservable {

  val t: Product

  def observables = t.productIterator.toSeq.asInstanceOf[Seq[JFXO]]
}

trait ObservableProxy extends Observable {
  val jfx: JFXO
  def addListener(listener: InvalidationListener) = jfx addListener listener
  def removeListener(listener: InvalidationListener) = jfx removeListener listener
}

trait ObservableImplicits {

  implicit class JfxObservablePimper(val jfx: JFXO) extends ObservableProxy

  implicit class Observable2[O1 <: JFXO, O2 <: JFXO](val t: (O1, O2)) extends TupleObservable {

    def apply[A, B](f: (O1, O2) => A)(implicit applier: Applier2[A, O1, O2, B]): B =
      applier apply (t, f)
  }

  implicit class Observable3[O1 <: JFXO, O2 <: JFXO, O3 <: JFXO](val t: (O1, O2, O3)) extends TupleObservable {

    def apply[A](f: (O1, O2, O3) => A) = new Func[A](t._1, t._2, t._3) {
      def compute = f(t._1, t._2, t._3)
    }
  }

  implicit class Observable4[O1 <: JFXO, O2 <: JFXO, O3 <: JFXO, O4 <: JFXO](val t: (O1, O2, O3, O4)) extends TupleObservable {

    def apply[A](f: (O1, O2, O3, O4) => A) = new Func[A](t._1, t._2, t._3, t._4) {
      def compute = f(t._1, t._2, t._3, t._4)
    }
  }

  implicit class Observable5[O1 <: JFXO, O2 <: JFXO, O3 <: JFXO, O4 <: JFXO, O5 <: JFXO](val t: (O1, O2, O3, O4, O5)) extends TupleObservable {

    def apply[A](f: (O1, O2, O3, O4, O5) => A) = new Func[A](t._1, t._2, t._3, t._4, t._5) {
      def compute = f(t._1, t._2, t._3, t._4, t._5)
    }
  }

  implicit class Observable6[O1 <: JFXO, O2 <: JFXO, O3 <: JFXO, O4 <: JFXO, O5 <: JFXO, O6 <: JFXO](val t: (O1, O2, O3, O4, O5, O6)) extends TupleObservable {

    def apply[A](f: (O1, O2, O3, O4, O5, O6) => A) = new Func[A](t._1, t._2, t._3, t._4, t._5, t._6) {
      def compute = f(t._1, t._2, t._3, t._4, t._5, t._6)
    }
  }

  //	And so on...

  implicit class SeqObservable(val observables: Seq[JFXO]) extends PluralObservable
}

object Observable extends ObservableImplicits

