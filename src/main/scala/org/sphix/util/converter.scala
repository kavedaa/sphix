package org.sphix.util

trait Converter[A, B] {
  
  def convert(a: A): Option[B]
  def deconvert(b: B): Option[A]
  
  def filterRight(p: B => Boolean) = new Converter[A, B] { outer =>
    def convert(a: A) = outer convert a filter p
    def deconvert(b: B) = outer deconvert b
  }
  
  def filterLeft(p: A => Boolean) = new Converter[A, B] { outer =>
    def convert(a: A) = outer convert a
    def deconvert(b: B) = outer deconvert b filter p
  }

  def filter(pa: A => Boolean, pb: B => Boolean) = new Converter[A, B] { outer =>
    def convert(a: A) = outer convert a filter pb
    def deconvert(b: B) = outer deconvert b filter pa
  }
  
  def preFilterRight(p: A => Boolean) = new Converter[A, B] { outer =>
    def convert(a: A) = if (p(a)) outer convert a else None
    def deconvert(b: B) = outer deconvert b
  }
  
  def preFilterLeft(p: B => Boolean) = new Converter[A, B] { outer =>
    def convert(a: A) = outer convert a
    def deconvert(b: B) = if (p(b)) outer deconvert b else None
  }

  def preFilter(pa: A => Boolean, pb: B => Boolean) = new Converter[A, B] { outer =>
    def convert(a: A) = if (pa(a)) outer convert a else None
    def deconvert(b: B) = if (pb(b)) outer deconvert b else None
  }  
}

trait LeftConverter[A, B] {
  def convert(a: A): Option[B]
  def deconvert(b: B): A  
}

trait RightConverter[A, B] {
  def convert(a: A): B
  def deconvert(b: B): Option[A]
}

trait FullConverter[A, B] {
  def conv(a: A): B
  def deconvert(b: B): A
}

abstract class StringConverter[A] extends javafx.util.StringConverter[A] with FullConverter[A, String] {
  def toString(a: A) = conv(a)
  def fromString(s: String): A = deconvert(s)
}

object Converter {
  
  def apply[A, B](c: A => Option[B], d: B => Option[A]) = new Converter[A, B] {
    def convert(a: A) = c(a)
    def deconvert(b: B) = d(b)
  }
}

object Filter {
  
  def apply[A](pl: A => Boolean, pr: A => Boolean) = new Converter[A, A] {
    def convert(l: A) = if (pl(l)) Some(l) else None
    def deconvert(r: A) = if (pr(r)) Some(r) else None    
  }
}