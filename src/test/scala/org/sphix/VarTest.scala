package org.sphix

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import javafx.beans.InvalidationListener
import javafx.beans.value.ChangeListener
import javafx.beans.property.SimpleObjectProperty

class VarTest extends FunSuite with ShouldMatchers {

  test("factory") {

    val p = Var(3)

    p() should equal(3)
  }

  test("set when not bound") {

    val p = Var(3)

    p() = 4

    p() should equal(4)
  }

  test("set same value - nothing should be fired") {

    var count = 0

    val p = Var(3)

    p observe { count += 1 }

    p() = 4

    count should equal(1)

    p() = 4

    count should equal(1)
  }

  test("bind") {

    val p = Var(3)
    val q = Var(4)

    p <== q

    p() should equal(4)
    p.isBound should be(true)

    q() = 5

    p() should equal(5)
  }

  test("unbind") {

    val p = Var(3)
    val q = Var(4)

    p <== q

    //	TODO!!
  }

  test("set when bound") {

    val p = Var(3)
    val q = Var(4)

    p <== q

    evaluating { p() = 5 } should produce[RuntimeException]

    p() should equal(4)
  }

  test("chained bind") {

    val p1 = Var(3)
    val p2 = Var(4)
    val p3 = Var(5)

    p1 <== p2

    p1() should equal(4)

    p2 <== p3

    p2() should equal(5)
    p1() should equal(5)

    p3() = 6

    p2() should equal(6)
    p1() should equal(6)
  }

  test("conversion from JFX property") {

    val p = new SimpleObjectProperty[Int](3)
    val q = Var(4)

    import Var._
    
    p <== q

    p() should equal(4)

    q() = 5

    p() should equal(5)
  }

  test("variance") {

    val v1 = Var[Option[Int]](None)

    // must ascribe super type for binding (et al?) to work
    val v2: Val[Option[Int]] = Val(Some(3))

    v1 <== v2
  }

}