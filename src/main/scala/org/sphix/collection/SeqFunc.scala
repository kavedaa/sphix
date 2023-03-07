package org.sphix.collection

import javafx.collections.FXCollections
import javafx.{ beans => jfxb }
import javafx.beans.InvalidationListener
import scala.jdk.CollectionConverters._


abstract class SeqFunc[A](dependencies: jfxb.Observable*) extends ObservableSeq[A] {

  protected val observableList = FXCollections.observableArrayList[A]

  def compute: Iterable[A]

  def toObservableList = FXCollections unmodifiableObservableList observableList

  def reEvaluate() = { observableList setAll compute.asJavaCollection }

  private val listener = new InvalidationListener {
    def invalidated(o: jfxb.Observable) = {
      reEvaluate()
    }
  }

  dependencies foreach { _ addListener listener }

  compute match {
    //  TODO we could make this the underlying list instead??? 
    case os: ObservableSeq[_] => os addListener listener
    case _ =>
  }
  
  reEvaluate()
}

object SeqFunc {

  type O = jfxb.Observable

  def apply[T <: O, R](o: T)(f: T => Iterable[R]) =
    new SeqFunc[R](o) {
      def compute = f(o)
    }

  def apply[T1 <: O, T2 <: O, R](o1: T1, o2: T2)(f: (T1, T2) => Iterable[R]) =
    new SeqFunc[R](o1, o2) {
      def compute = f(o1, o2)
    }

  def apply[T1 <: O, T2 <: O, T3 <: O, R](o1: T1, o2: T2, o3: T3)(f: (T1, T2, T3) => Iterable[R]) =
    new SeqFunc[R](o1, o2, o3) {
      def compute = f(o1, o2, o3)
    }

  def apply[T1 <: O, T2 <: O, T3 <: O, T4 <: O, R](o1: T1, o2: T2, o3: T3, o4: T4)(f: (T1, T2, T3, T4) => Iterable[R]) =
    new SeqFunc[R](o1, o2, o3, o4) {
      def compute = f(o1, o2, o3, o4)
    }

  def apply[T1 <: O, T2 <: O, T3 <: O, T4 <: O, T5 <: O, R](o1: T1, o2: T2, o3: T3, o4: T4, o5: T5)(f: (T1, T2, T3, T4, T5) => Iterable[R]) =
    new SeqFunc[R](o1, o2, o3, o4, o5) {
      def compute = f(o1, o2, o3, o4, o5)
    }

  def apply[T1 <: O, T2 <: O, T3 <: O, T4 <: O, T5 <: O, T6 <: O, R](o1: T1, o2: T2, o3: T3, o4: T4, o5: T5, o6: T6)(f: (T1, T2, T3, T4, T5, T6) => Iterable[R]) =
    new SeqFunc[R](o1, o2, o3, o4, o5, o6) {
      def compute = f(o1, o2, o3, o4, o5, o6)
    }

  def apply[T1 <: O, T2 <: O, T3 <: O, T4 <: O, T5 <: O, T6 <: O, T7 <: O, R](o1: T1, o2: T2, o3: T3, o4: T4, o5: T5, o6: T6, o7: T7)(f: (T1, T2, T3, T4, T5, T6, T7) => Iterable[R]) =
    new SeqFunc[R](o1, o2, o3, o4, o5, o6, o7) {
      def compute = f(o1, o2, o3, o4, o5, o6, o7)
    }

  def apply[T1 <: O, T2 <: O, T3 <: O, T4 <: O, T5 <: O, T6 <: O, T7 <: O, T8 <: O, R](o1: T1, o2: T2, o3: T3, o4: T4, o5: T5, o6: T6, o7: T7, o8: T8)(f: (T1, T2, T3, T4, T5, T6, T7, T8) => Iterable[R]) =
    new SeqFunc[R](o1, o2, o3, o4, o5, o6, o7, o8) {
      def compute = f(o1, o2, o3, o4, o5, o6, o7, o8)
    }

  def apply[T1 <: O, T2 <: O, T3 <: O, T4 <: O, T5 <: O, T6 <: O, T7 <: O, T8 <: O, T9 <: O, R](o1: T1, o2: T2, o3: T3, o4: T4, o5: T5, o6: T6, o7: T7, o8: T8, o9: T9)(f: (T1, T2, T3, T4, T5, T6, T7, T8, T9) => Iterable[R]) =
    new SeqFunc[R](o1, o2, o3, o4, o5, o6, o7, o8, o9) {
      def compute = f(o1, o2, o3, o4, o5, o6, o7, o8, o9)
    }

  def apply[T1 <: O, T2 <: O, T3 <: O, T4 <: O, T5 <: O, T6 <: O, T7 <: O, T8 <: O, T9 <: O, T10 <: O, R](o1: T1, o2: T2, o3: T3, o4: T4, o5: T5, o6: T6, o7: T7, o8: T8, o9: T9, o10: T10)(f: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) => Iterable[R]) =
    new SeqFunc[R](o1, o2, o3, o4, o5, o6, o7, o8, o9, o10) {
      def compute = f(o1, o2, o3, o4, o5, o6, o7, o8, o9, o10)
    }

  //	And so on...
}