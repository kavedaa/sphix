package org.sphix

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers

class ObservingValTest extends FunSuite with ShouldMatchers {

    test("observing inner observables") {
      
      case class Person(name: Var[String], age: Var[Int]) {
        def fields = (name, age)
      }
      
      val john = Person(Var("John"), Var(30))
      
      val v = Val(john, (p: Person) => p.fields)		//	don't know why we need type annotation here
      
      var count = 0
      
      v observe { count += 1 }
      
      john.name() = "Johannes"
        
      count should equal(1)
      
      john.age() = 43
      
      count should equal(2)
    }
    
  
  
}