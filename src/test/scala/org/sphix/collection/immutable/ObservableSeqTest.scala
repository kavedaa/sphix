package org.sphix.collection.immutable

import org.scalatest._

class ObservableSeqTest extends FunSuite with Matchers {
  
  test("factory") {
    
    ObservableSeq[Nothing]() shouldEqual Nil
       
    ObservableSeq('A, 'B) should equal(ObservableSeq('A, 'B))    
  }
  
  test("generic collection methods") {
    
    ObservableSeq('A, 'B).toSeq should equal(Seq('A, 'B))

    ObservableSeq('A, 'B).to[Seq] should equal(Seq('A, 'B))
    
    Seq('A, 'B).to[ObservableSeq] should equal(ObservableSeq('A, 'B))
  }
  
  test("implicit conversion to ObservableList") {
    
    val ol: javafx.collections.ObservableList[Symbol] = ObservableSeq('A, 'B)
    
    ol get 0 should equal('A)
    ol get 1 should equal('B)
  }
  
  
}
