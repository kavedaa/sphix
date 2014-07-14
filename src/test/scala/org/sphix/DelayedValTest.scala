package org.sphix

import org.scalatest._
import java.util.concurrent.TimeUnit

class DelayedValTest extends FunSuite with Matchers {

  test("propagation") {

    val source = Var(3)

    val delayed = new DelayedVal(source, 1000, TimeUnit.MILLISECONDS) {
      //	override runLater so we can test it without firing up JavaFX
      override def runLater[U](r: => U) { r }
    }

    delayed() shouldEqual 3

    source() = 4
    delayed() shouldEqual 3		//	assuming it takes less than a second to execute these lines

    Thread sleep 1200 			//	give it some extra time
    delayed() shouldEqual 4
  }

  test("initial laziness") {

    var count = 0

    val source = Var(3)
    val computed = source map { s => count += 1; s }

    val delayed = new DelayedVal(computed, 1000, TimeUnit.MILLISECONDS) {
      override def runLater[U](r: => U) { r }
    }

    count shouldEqual 0
    
    delayed()    
    count shouldEqual 1
  }
  
  test("change delay time") {

    val source = Var(3)

    val delayed = new DelayedVal(source, 1000, TimeUnit.MILLISECONDS) {
      override def runLater[U](r: => U) { r }
    }
    
    delayed() shouldEqual 3
    
    source() = 4    
    delayed() shouldEqual 3
    
    delayed.delayTime() = 2000    
    Thread sleep 1200  
    delayed() shouldEqual 3
    
    Thread sleep 1200    
    delayed() shouldEqual 4
  }
}