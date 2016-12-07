package org.sphix

import org.scalatest._

class FuncTest extends FunSuite with Matchers {

  test("computation") {
    
    val a = Var(3)
    
    val func = new Func[Int](a) { def compute = a() * 2 }
    
    func() shouldEqual 6
    
    a() = 4
    
    func() shouldEqual 8
  }
  
  test("initialization laziness") {
    
    var count = 0
    
    def a() = { count += 1; "foo" }
    
    val dummy = Val("")
    
    val func = new Func[String](dummy) { def compute = a() }

    count shouldEqual 0
    
    func() should equal("foo")
    
    count shouldEqual 1    
  }
}