package org.sphix

import javafx.{ beans => jfxb }

abstract class Func[A](dep: jfxb.Observable, deps: jfxb.Observable*) extends LazyVal[A] {

  val dependencies = dep +: deps
  
  dependencies foreach (_ addListener lazyListener)

  def dispose() {
    dependencies foreach (_ removeListener lazyListener)
  }
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