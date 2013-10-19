package org.sphix.collection

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import org.sphix.collection.mutable.ObservableBuffer
import javafx.beans.InvalidationListener
import javafx.beans.Observable
import org.sphix.Val

class ObservableSeqTest extends FunSuite with ShouldMatchers {

  test("factory") {

    ObservableSeq() should equal(Nil)

    ObservableSeq('A, 'B) should equal(ObservableSeq('A, 'B))
  }

  test("generic collection methods") {

    ObservableSeq('A, 'B).toSeq should equal(Seq('A, 'B))

    ObservableSeq('A, 'B).to[Seq] should equal(Seq('A, 'B))

    Seq('A, 'B).to[ObservableSeq] should equal(ObservableSeq('A, 'B))
  }

  test("samples of inherited standard collection methods") {

    val os = ObservableSeq(1, 2, 3, 4, 5)

    os filter (_ > 3) should equal(ObservableSeq(4, 5))

    os map (_ * 2) should equal(ObservableSeq(2, 4, 6, 8, 10))
  }

  test("apply") {

    val os = ObservableSeq('A, 'B)

    os(0) should equal('A)
    os(1) should equal('B)
  }

  test("length") {
    ObservableSeq().length should equal(0)
    ObservableSeq('A, 'B).length should equal(2)
  }

  test("invalidation listener") {

    var count = 0

    val listener = new InvalidationListener {
      def invalidated(o: Observable) {
        count += 1
      }
    }

    val ob = ObservableBuffer('A)

    ob addListener listener
    ob += 'B
    count should equal(1)

    ob removeListener listener
    ob += 'C
    count should equal(1)
  }

  test("observe") {

    var count = 0

    val ob = ObservableBuffer('A)

    val obs = ob observe {
      count += 1
    }

    ob += 'B
    count should equal(1)

    obs dispose ()
    ob += 'C
    count should equal(1)
  }

  test("onChange") {

    var count = 0

    val ob = ObservableBuffer('A)

    val obs = ob onChange { changes =>
      count += 1
    }

    ob += 'B
    count should equal(1)

    obs dispose ()
    ob += 'C
    count should equal(1)
  }

  test("onAdded") {

    var added: Seq[Symbol] = Nil

    val ob = ObservableBuffer('A)

    ob onAdded { s =>
      added = s
    }

    ob ++= Seq('B, 'C)

    added should equal(Seq('B, 'C))
  }

  test("onRemoved") {

    var removed: Seq[Symbol] = Nil

    val ob = ObservableBuffer('A, 'B, 'C)

    ob onRemoved { s =>
      removed = s
    }

    ob --= Seq('B, 'C)

    removed should equal(Seq('B, 'C))
  }

  test("implicit conversion to ObservableList") {

    val ol: javafx.collections.ObservableList[Symbol] = ObservableSeq('A, 'B)

    ol get 0 should equal('A)
    ol get 1 should equal('B)
  }

  test("modify underlying unmodifiable ObservableList") {

    val ol = ObservableSeq('A, 'B).toObservableList

    evaluating {
      ol add 'C
    } should produce[UnsupportedOperationException]
  }

}