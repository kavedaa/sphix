package org.sphix

import org.scalatest._

class InvalidationObserverTest extends FeatureSpec with Matchers {

  feature("ignore") {
    
    scenario("single") {
      
      var count = 0
      
      val foo = Var(3)
      
      val observer = foo observe { count += 1 }
      
      foo() = 4
      count shouldEqual 1
      
      observer ignore { foo() = 5 }
      count shouldEqual 1
    }
  }
  
    scenario("plural") {
      
      var count = 0
      
      val foo, bar = Var(3)
      
      val observer = (foo, bar) observe { count += 1 }
      
      foo() = 4
      count shouldEqual 1
      
      observer ignore { foo() = 5 }
      count shouldEqual 1
      
      observer ignore { bar() = 5 }
      count shouldEqual 1      
    }
  
}