package org.sphix.collection.mutable

import org.scalatest._
import org.sphix.collection.Change
import Change._

class ObservableStackTest extends FunSuite with Matchers with BeforeAndAfter {

  val stack = ObservableStack[Symbol]()

  var changes: Seq[Change[Symbol]] = Nil
  var count = 0

  stack onChange { cs =>
    changes = cs
    count += 1
  }

  before {
    stack clear ()
    stack pushAll Seq('A, 'B)
    changes = Nil
    count = 0
  }

  test("factory methods") {

    ObservableStack[Nothing]().toList shouldEqual Nil

    ObservableStack('A, 'B).toList shouldEqual List('A, 'B)

    ObservableStack('A, 'B).to[List] shouldEqual List('A, 'B)

    List('A, 'B).to[ObservableStack] shouldEqual ObservableStack('A, 'B)

    ObservableStack('A, 'B) shouldEqual ObservableStack('A, 'B)
  }

  test("push") {

    stack push 'C

    count shouldEqual 1
    changes should equal(Seq(Added(0, Seq('C))))
    stack.toList shouldEqual List('C, 'B, 'A)
  }

  test("pushAll") {

    stack pushAll Seq('C, 'D)

    count shouldEqual 1
    changes shouldEqual (Seq(Added(0, Seq('D, 'C))))
    stack.toList shouldEqual List('D, 'C, 'B, 'A)

  }

  test("push vararg") {

    stack push ('C, 'D, 'E)

    count shouldEqual 1
    changes shouldEqual (Seq(Added(0, Seq('E, 'D, 'C))))
    stack.toList shouldEqual List('E, 'D, 'C, 'B, 'A)
  }

  test("pop") {

    val b = stack pop ()

    b shouldEqual 'B
    count shouldEqual 1
    changes shouldEqual (Seq(Removed(0, Seq('B))))

    val a = stack pop ()

    a shouldEqual 'A
    count shouldEqual 2
    changes shouldEqual (Seq(Removed(0, Seq('A))))

    intercept[NoSuchElementException] {
      stack pop ()
    }  

  }
}
