package org.sphix

import javafx.{ beans => jfxb }
import javafx.beans.value.{ ObservableValue => OV }

abstract class Func[A](protected val dependencies0: jfxb.Observable*) extends LazyVal[A] {

  //  Make it overridable
  def dependencies = dependencies0

  dependencies foreach (_ addListener lazyListener)

  def dispose() = {
    dependencies foreach (_ removeListener lazyListener)
  }
}

class Func1[B, A](d1: OV[B], f: B => A) extends Func[A](d1) {
  def compute = f(d1.getValue)
}

object Func {

  type O = jfxb.Observable

  def apply[T <: O, R](o: T)(f: T => R) =
    new Func[R](o) {
      def compute = f(o)
    }

  def apply[T1 <: O, T2 <: O, R](o1: T1, o2: T2)(f: (T1, T2) => R) =
    new Func[R](o1, o2) {
      def compute = f(o1, o2)
    }

  def apply[T1 <: O, T2 <: O, T3 <: O, R](o1: T1, o2: T2, o3: T3)(f: (T1, T2, T3) => R) =
    new Func[R](o1, o2, o3) {
      def compute = f(o1, o2, o3)
    }

  //	And so on...
}