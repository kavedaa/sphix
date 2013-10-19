package org.sphix

class SimpleVar[A](initValue: A, name: String, bean: AnyRef) extends Var[A] with VarImpl[A] {

  value = initValue

  def getName = name
  def getBean = bean
}