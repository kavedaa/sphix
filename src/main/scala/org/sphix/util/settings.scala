package org.sphix.util

import java.io._
import java.util.Properties
import javafx.beans.property.ObjectPropertyBase
import javafx.beans.InvalidationListener
import javafx.beans.Observable
import scala.util.Try

abstract class IniSettings(name: String) {

  val file = new File(name + ".ini")
  
  protected abstract class Converter[A] {
    def conv(s: String): Option[A]
    def deconv(a: A): String
  }

  protected object Converter {
    implicit def stringConv = new Converter[String] {
      def conv(s: String) = Some(s)
      def deconv(s: String) = s
    }
  }

  protected class IniProperty[A](name: String)(implicit converter: Converter[A]) extends ObjectPropertyBase[Option[A]](None) {

    def getBean = null
    def getName = name

    def load(properties: Properties) {
      setValue(Option(properties getProperty name) flatMap converter.conv)
    }

    def store(properties: Properties) {
      getValue foreach (value => properties setProperty (name, converter deconv value))
    }
  }

  protected val settings: Seq[IniProperty[_]]

  private def load() {
    Try {
      val properties = new Properties
      properties load (new FileInputStream(file))
      settings foreach (_ load properties)
    }
  }
  
  private def store() {
    Try {
      val properties = new Properties
      settings foreach (_ store properties)
      properties store (new FileOutputStream(file), "")
    }
  }
  
  private val storeListener = new InvalidationListener {
    def invalidated(o: Observable) {
      store()
    }
  }
  
  def init() {
    load()
    settings foreach(_ addListener storeListener)
  }
}

