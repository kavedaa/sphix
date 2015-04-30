package org.sphix.collection

import javafx.collections.FXCollections
import javafx.{ beans => jfxb }
import javafx.beans.InvalidationListener
import scala.collection.JavaConversions

abstract class SeqFunc[A](dependencies: jfxb.Observable*) extends ObservableSeq[A] {

  protected val observableList = FXCollections.observableArrayList[A]

  def compute: Iterable[A]

  def toObservableList = FXCollections unmodifiableObservableList observableList

  def reEvaluate() = { observableList setAll (JavaConversions asJavaCollection compute) }

  private val listener = new InvalidationListener {
    def invalidated(o: jfxb.Observable) {
      reEvaluate()
    }
  }

  dependencies foreach { _ addListener listener }

  reEvaluate()
}

object SeqFunc {

  type O = jfxb.Observable

  def apply[T <: O, R](o: T)(f: T => Seq[R]) =
    new SeqFunc[R](o) {
      def compute = f(o)
    }

  def apply[T1 <: O, T2 <: O, R](o1: T1, o2: T2)(f: (T1, T2) => Seq[R]) =
    new SeqFunc[R](o1, o2) {
      def compute = f(o1, o2)
    }

  //	And so on...
}