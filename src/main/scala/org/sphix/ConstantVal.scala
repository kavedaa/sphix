package org.sphix

class ConstantVal[A](val getValue: A) extends Val[A] with ValImpl[A]