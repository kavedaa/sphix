# sphix

*sphix* is a library that simplifies JavaFX development with Scala, by addding
idiomatic Scala syntax to commonly used JavaFX features such as properties and collections.
It is not a complete Scala wrapper on top of JavaFX like e.g. ScalaFX. 

*sphix* is very much a work in progress, and the API should not be considered stable.

Currently it has the following features:

* Reimplemented and enhanced observable values, a.k.a. "properties and bindings".
* Wrappers for and enhancements to observable collections.
* Composable cell factories.
* Various other utilities.

## Quick overview

### Observable values

```scala
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

```

### Observable collections

```scala
val xs = ObservableSeq(1, 2, 3)

val size = xs(_.size)		//	size is a Val[Int]
```
