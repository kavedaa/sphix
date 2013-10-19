package org.sphix.collection.immutable

import scala.collection.SeqLike
import scala.collection.generic.GenericTraversableTemplate
import scala.collection.generic.SeqFactory
import scala.collection.mutable.Builder
import scala.collection.JavaConversions
import javafx.collections.FXCollections
import javafx.beans.Observable
import javafx.util.Callback

trait ObservableSeqLike[A]
  extends org.sphix.collection.ObservableSeq[A] {

  def toObservableList = FXCollections unmodifiableObservableList observableList
}

trait ObservableSeqBuilder[A, T]
  extends Builder[A, T] { this: ObservableSeqLike[A] =>

  def +=(elem: A) = {
    observableList add elem
    this
  }

  def clear() { observableList clear () }
}

class ObservableSeq[A](protected val observableList: javafx.collections.ObservableList[A])
  extends org.sphix.collection.ObservableSeq[A]
  with Seq[A]
  with SeqLike[A, ObservableSeq[A]]
  with ObservableSeqLike[A]
  with GenericTraversableTemplate[A, ObservableSeq] {

  override def companion = ObservableSeq
}

object ObservableSeq extends SeqFactory[ObservableSeq] {

  def newBuilder[A] =
    new ObservableSeq[A](FXCollections.observableArrayList[A]) with ObservableSeqBuilder[A, ObservableSeq[A]] {
      def result = this
    }

  implicit def cbf[A] = new GenericCanBuildFrom[A] {
    override def apply() = newBuilder[A]
  }

  implicit def toObservableList[A](os: ObservableSeq[A]) = os.toObservableList
}

