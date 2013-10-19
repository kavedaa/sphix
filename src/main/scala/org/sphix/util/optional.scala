package org.sphix.util

sealed abstract class Optional[+A] {
  def ifPresent[U](f: A => U)
}

case class Present[+A](x: A) extends Optional[A] {
  def ifPresent[U](f: A => U) { f(x) }  
}

case object Absent extends Optional[Nothing] {
    def ifPresent[U](f: Nothing => U) {}
}

object Optional {
  implicit def fromValue[A](x: A) = Present(x)
}