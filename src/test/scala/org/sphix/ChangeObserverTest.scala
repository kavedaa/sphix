package org.sphix

import org.scalatest._

class ChangeObserverTest extends FeatureSpec with Matchers {

  feature("ignore") {
    
    scenario("single") {
      
      var count = 0
      
      val foo = Var(3)
      
      val observer = foo onValue { _ => count += 1 }
      
      foo() = 4
      count shouldEqual 1
      
      observer ignore { foo() = 5 }
      count shouldEqual 1
    }
    
  }
}