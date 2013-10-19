# sphix

*sphix* is a library that simplifies JavaFX development with Scala. Its goal is to "Scalaify" JavaFX as much as is needed to create a good development experience, focusing on the most-used features and patterns, without creating a complete Scala-layer on top of it.

*sphix* is very much a work in progress, developing with the needs of the author. Currently it sports the following features:

* Reimplemented and enhanced observables, a.k.a. "properties and bindings".
* Wrappers for and enhancements to observable collections.
* Composable cell factories.
* Various other utilities.

## Quick overview

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



## Observables, a.k.a. properties and bindings

Without too much ado, let's what you can to with these in *sphix*. We'll start somewhere in the middle, with `Var`, which is *sphix*'s implementation of JavaFX's `Property` interface:

```scala
  val name = Var("Tom")
```

Really simple, yes? You get and set it with Scala's standard apply/update syntax:

```scala
  println(name())
  
  name() = "John"
```

Ok, that's trivial. Let's observe it:

```scala
  name observe { println("name has changed!") }
```

A bit easier than adding a listener. There are variations of this:

```scala
  name onValue { v => println(v) }

  //	Or simply
  name onValue println
  
  name onChange { (o, oldName, newName) => println(s"name changed from $oldName to $newName") }
```

You can of course bind it as well (borrowing symbolic operator from ScalaFX, thanks!):

```scala
  val otherName = Var[String]()

  otherName <== name
```






