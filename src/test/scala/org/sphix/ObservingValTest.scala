package org.sphix

import org.scalatest._

class ObservingValTest extends FunSuite with Matchers {

    test("observing inner observables") {
      
      import Val._
      
      case class Person(name: Var[String], age: Var[Int]) {
        def fields = (name, age)
      }
      
      val john = Person(Var("John"), Var(30))
      
      val v = john observing(_.fields)
      
      var count = 0
      
      v observe { count += 1 }
      
      john.name() = "Johannes"
        
      count should equal(1)
      
      john.age() = 43
      
      count should equal(2)
    }
    
  
  
}