package org.sphix

import org.scalatest._

class FirableValTest extends FeatureSpec with Matchers {

  feature("atomic") {

    scenario("func") {

      var count = 0

      val foo = Var(3)
      val bar = Var(4)
      val zip = (foo, bar) map (_ + _)

      zip observe { count += 1 }

      foo() = 5
      bar() = 6
      
      count shouldEqual 2

      count = 0
      
      zip atomic {
        foo() = 7
        bar() = 8
      }

      count shouldEqual 1
    }

  }
}