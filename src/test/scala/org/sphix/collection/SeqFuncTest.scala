package org.sphix.collection

import org.scalatest._
import org.sphix._
import org.sphix.collection.mutable.ObservableBuffer

class SeqFuncTest extends FunSuite with Matchers {

  test("depend on a Var") {

    val v = Var(3)

    val seq = SeqFunc(v) { v => Seq(v(), v()) }

    seq shouldEqual Seq(3, 3)

    v() = 4
    seq shouldEqual Seq(4, 4)
  }

  test("depend on an ObservableBuffer") {

    val ob = ObservableBuffer(1, 2, 3)

    val seq = SeqFunc(ob) { _.reverse }

    seq shouldEqual Seq(3, 2, 1)

    ob += 4
    seq shouldEqual Seq(4, 3, 2, 1)
  }

  test("depend on two Vars") {

    val v1 = Var(3)
    val v2 = Var(4)

    val seq = SeqFunc(v1, v2) { (v1, v2) => Seq(v1(), v2()) }

    seq shouldEqual Seq(3, 4)

    v1() = 5
    v2() = 6
    seq shouldEqual Seq(5, 6)
  }

  test("depend on a Var and an ObservableBuffer") {

    val v = Var(3)
    val ob = ObservableBuffer(1, 2, 3, 4, 5)

    val seq = SeqFunc(v, ob) { (v, ob) => ob take v() }

    seq shouldEqual Seq(1, 2, 3)

    v() = 4
    seq shouldEqual Seq(1, 2, 3, 4)

    0 +=: ob
    seq should equal(Seq(0, 1, 2, 3))
  }

  test("depend on two ObservableBuffers") {

    val ob1 = ObservableBuffer(1, 2, 3)
    val ob2 = ObservableBuffer(4, 5, 6)

    val seq = SeqFunc(ob1, ob2) { _ ++ _ }

    seq shouldEqual Seq(1, 2, 3, 4, 5, 6)

    ob1 -= 2
    ob2 += 7
    seq shouldEqual Seq(1, 3, 4, 5, 6, 7)
  }

  test("computed is ObservableSeq") {

    val v = Val(ObservableBuffer(1, 2))

    //  TODO should find a way to simplify this!!!
    val seq = SeqFunc(v) { x => SeqFunc(x()) { _ map (_ * 2) } }
    
    seq shouldEqual Seq(2, 4)
    
    var ev = false
    
    seq observe { ev = true }
    
    v() add 3
            
    seq shouldEqual Seq(2, 4, 6)
    
    ev shouldBe true
  }
}