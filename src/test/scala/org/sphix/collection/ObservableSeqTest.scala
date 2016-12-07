package org.sphix.collection

import org.scalatest._
import org.sphix.collection.mutable.ObservableBuffer
import javafx.beans.InvalidationListener
import javafx.beans.Observable
import org.sphix.Val

class ObservableSeqTest extends FunSuite with Matchers {

  test("factory") {

    ObservableSeq[Nothing]() shouldEqual Nil

    ObservableSeq('A, 'B) shouldEqual ObservableSeq('A, 'B)
  }

  test("generic collection methods") {

    ObservableSeq('A, 'B).toSeq shouldEqual Seq('A, 'B)

    ObservableSeq('A, 'B).to[Seq] shouldEqual Seq('A, 'B)

    Seq('A, 'B).to[ObservableSeq] shouldEqual ObservableSeq('A, 'B)
  }

  test("samples of inherited standard collection methods") {

    val os = ObservableSeq(1, 2, 3, 4, 5)

    os filter (_ > 3) shouldEqual ObservableSeq(4, 5)

    os map (_ * 2) shouldEqual ObservableSeq(2, 4, 6, 8, 10)
  }

  test("apply") {

    val os = ObservableSeq('A, 'B)

    os(0) shouldEqual 'A
    os(1) shouldEqual 'B
  }

  test("length") {
    ObservableSeq().length shouldEqual 0
    ObservableSeq('A, 'B).length shouldEqual 2
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
    count shouldEqual 1

    ob removeListener listener
    ob += 'C
    count shouldEqual 1
  }

  test("observe") {

    var count = 0

    val ob = ObservableBuffer('A)

    val obs = ob observe {
      count += 1
    }

    ob += 'B
    count shouldEqual 1

    obs dispose ()
    ob += 'C
    count shouldEqual 1
  }

  test("onChange") {

    var count = 0

    val ob = ObservableBuffer('A)

    val obs = ob onChange { changes =>
      count += 1
    }

    ob += 'B
    count shouldEqual 1 

    obs dispose ()
    ob += 'C
    count shouldEqual 1
  }

  test("onAdded") {

    var added: Seq[Symbol] = Nil

    val ob = ObservableBuffer('A)

    ob onAdded { s =>
      added = s
    }

    ob ++= Seq('B, 'C)

    added shouldEqual Seq('B, 'C)
  }

  test("onRemoved") {

    var removed: Seq[Symbol] = Nil

    val ob = ObservableBuffer('A, 'B, 'C)

    ob onRemoved { s =>
      removed = s
    }

    ob --= Seq('B, 'C)

    removed shouldEqual Seq('B, 'C)
  }

  test("implicit conversion to ObservableList") {

    val ol: javafx.collections.ObservableList[Symbol] = ObservableSeq('A, 'B)

    ol get 0 shouldEqual 'A
    ol get 1 shouldEqual 'B
  }

  test("modify underlying unmodifiable ObservableList") {

    val ol = ObservableSeq('A, 'B).toObservableList

    intercept[UnsupportedOperationException] {
      ol add 'C
    } 
  }

  test("distinctBy") {
    
    case class Person(name: String, age: Int)
    
    val os = ObservableSeq(Person("John", 34), Person("Tom", 54), Person("John", 23))
    
    os distinctBy(_.name) shouldEqual Seq(Person("John", 34), Person("Tom", 54))
  }
}