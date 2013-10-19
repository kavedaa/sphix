package org.sphix

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers

class FuncTest extends FunSuite with ShouldMatchers {

  test("computation") {
    
    val a = Var(3)
    
    val func = new Func[Int](a) { def compute = a() * 2 }
    
    func() should equal(6)
    
    a() = 4
    
    func() should equal(8)
  }
  
  test("initialization laziness") {
    
    var count = 0
    
    def a() = { count += 1; "foo" }
    
    val dummy = Val("")
    
    val func = new Func[String](dummy) { def compute = a() }

    count should equal(0)
    
    func() should equal("foo")
    
    count should equal(1)    
  }
}