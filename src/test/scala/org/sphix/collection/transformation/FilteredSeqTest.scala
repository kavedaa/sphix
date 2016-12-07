package org.sphix.collection.transformation

import org.scalatest._
import org.sphix.collection.mutable.ObservableBuffer
import org.sphix.collection.ObservableSeq
import javafx.beans.property.SimpleObjectProperty

class FilteredSeqTest extends FeatureSpec with Matchers {

  feature("static filter") {

    val ob = ObservableBuffer(1, 2, 3, 4, 5)

    scenario("all-pass filter") {

      val filtered = new FilteredSeq(ob, (i: Int) => true)

      filtered shouldEqual Seq(1, 2, 3, 4, 5)
    }

    scenario("no-pass filter") {

      val filtered = new FilteredSeq(ob, (i: Int) => false)

      filtered shouldEqual Nil
    }

    scenario("simple filter") {

      val filtered = new FilteredSeq(ob, (i: Int) => i > 3)

      filtered shouldEqual Seq(4, 5)
    }
  }

  feature("mutate the filter") {

    val ob = ObservableBuffer(1, 2, 3, 4, 5)

    val filter = new SimpleObjectProperty[Int => Boolean]((i: Int) => true)

    val filtered = new FilteredSeq(ob, filter)

    scenario("all-pass filter") {

      filter setValue { (i: Int) => true }

      filtered shouldEqual Seq(1, 2, 3, 4, 5)
    }

    scenario("no-pass filter") {

      filter setValue { (i: Int) => false }

      filtered shouldEqual Nil
    }

    scenario("simple filter") {

      filter setValue { (i: Int) => i > 3 }

      filtered shouldEqual Seq(4, 5)
    }

  }

}