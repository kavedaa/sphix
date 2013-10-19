package org.sphix.examples

import org.sphix._

object Examples {

  val foo = Val(3)	//	implements javafx.beans.value.ObservableValue
  
  val bar = Var(5)	//	implements javafx.beans.property.Property
  
  bar() = foo()		//	write and read using Scala's update/apply syntax
  
  bar() = 6
  
  bar observe { println("bar changed!") }
  
  (foo, bar) observe { println("foo or bar changed!") }
  
  bar onChange { (_, o, n) => println(s"bar change from $o to $n") }
  
  bar onValue { v => println(s"bar changed to $v") }
  
  bar onValue println		
  
  (foo, bar) onValue { (f, b) => println(s"foo or bar have new values: $f and $b") }
  
  val zip = bar map(_ * 2)
  
  val zap = for { b <- bar } yield b * 2	//	alternate syntax for previous line
  
  val pak = (foo, bar) map(_ + _)
  
  bar <== foo		//	alias for bind

  
}

object Test {
  
  object MyOption {
    def fromBoolean[A](b: Boolean)(a: => A) =
      if (b) Some(a) else None
  }    
  
  val o = MyOption.fromBoolean(true) {
    val x = 1
    val y = 2
    x + y
  }
}