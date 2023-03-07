package org.sphix

import javafx.beans.{ Observable => JFXO }
import org.sphix.collection._

trait Applier[A, O <: JFXO, B] {
  def apply(o: O, f: O => A): B
}

object Applier {
  
  implicit def defaultApplier[A, O <: JFXO]: Applier[A, O, Val[A]] = new Applier[A, O, Val[A]] {
    def apply(o: O, f: O => A) = new Func[A](o) {
      def compute = f(o)
    }
  }
  
//  implicit def iterableApplier[A, O <: jfxb.Observable] = new Applier[Iterable[A], O, ObservableSeq[A]] {
//    def apply(o: O, f: O => Iterable[A]) = new SeqFunc[A](o) {
//      def compute = f(o)
//    }
//  }
  
}

trait Applier2[A, O1 <: JFXO, O2 <: JFXO, B] {
  def apply(t: (O1, O2), f: (O1, O2) => A): B
}

object Applier2 {
  
  implicit def defaultApplier[A, O1 <: JFXO, O2 <: JFXO]: Applier2[A, O1, O2, Val[A]] = new Applier2[A, O1, O2, Val[A]] {
    def apply(t: (O1, O2), f: (O1, O2) => A) = new Func[A](t._1, t._2) {
      def compute = f(t._1, t._2)
    }
  }
}
