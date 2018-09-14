package org.sphix

import org.scalatest._
import javafx.beans.property.SimpleStringProperty
import org.sphix.util.FullConverter
import javafx.beans.InvalidationListener
import javafx.beans.property.SimpleBooleanProperty

class ConvertedVarTest extends FunSuite with Matchers {

  val converter = FullConverter[String, String](_.toUpperCase, _.toLowerCase)
  
  test("initalization") {

    val remote = new SimpleStringProperty("hello")
    val converted = new ConvertedVar(remote, converter)

    converted() shouldEqual "HELLO"
  }

  test("set value on remote") {

    val remote = new SimpleStringProperty("hello")
    val converted = new ConvertedVar(remote, converter)

    remote setValue "goodbye"

    converted() shouldEqual "GOODBYE"
  }

  test("set value on converted") {

    val remote = new SimpleStringProperty("hello")
    val converted = new ConvertedVar(remote, converter)

    converted() = "GOODBYE"

    remote.getValue shouldEqual "goodbye"
  }
  
  test("invalidation listener on remote") {
    
    val remote = new SimpleStringProperty("hello")
    val converted = new ConvertedVar(remote, converter)

    var count = 0 
    
    remote addListener new InvalidationListener {
      def invalidated(o: javafx.beans.Observable) {
        count += 1
      }
    }
    
    converted() = "GOODBYE"
      
    count shouldEqual 1
  }
  
  test("invalidation listener on converted") {
    
    val remote = new SimpleStringProperty("hello")
    val converted = new ConvertedVar(remote, converter)

    var count = 0 
    
    converted observe { count += 1 }
    
    remote setValue "goodbye"
    
    count shouldEqual 1    
  }
  
  test("change listener on converted") {
    
    val remote = new SimpleStringProperty("hello")
    val converted = new ConvertedVar(remote, converter)

    var evidence = ""
      
    converted onValue { evidence = _ }    
    
    remote setValue "goodbye"
    
    evidence shouldEqual "GOODBYE"
  }

  test("bind remote") {
    
    val remote = new SimpleStringProperty("hello")
    val converted = new ConvertedVar(remote, converter)
    
    val thirdparty = Var("first")
    
    remote bind thirdparty
    
    converted() shouldEqual "FIRST"
    
    thirdparty() = "second"
      
    converted() shouldEqual "SECOND"
  }
  
  test("bind converted") {
    
    val remote = new SimpleStringProperty("hello")
    val converted = new ConvertedVar(remote, converter)
    
    val thirdparty = Var("FIRST")
    
    converted <== thirdparty
    
    remote.getValue shouldEqual "first"
    
    thirdparty() = "SECOND"
      
    remote.getValue shouldEqual "second"  
  }
  
  test("bind to converted") {
    
    val remote = new SimpleStringProperty("hello")
    val converted = new ConvertedVar(remote, converter)

    val thirdparty = Var("")
    
    thirdparty <== converted
    
    thirdparty() shouldEqual "HELLO"
    
    remote setValue "goodbye"
    
    thirdparty() shouldEqual "GOODBYE"
  }
  
  test("bind bidirectionally") {
    
    val remote = new SimpleStringProperty("hello")
    val converted = new ConvertedVar(remote, converter)

    val thirdparty = Var("")
    
    thirdparty <==> converted
    
    thirdparty() shouldEqual "HELLO"
    
    thirdparty() = "GOODBYE"
      
    remote.getValue shouldEqual "goodbye"
    
    remote setValue "hello again"
    
    thirdparty() shouldEqual "HELLO AGAIN"
  }
  
  test("implicit converter") {
    
    val jfx = new SimpleBooleanProperty(true)
    
    val remote = Var(false)
    
    val converted = remote.convert[java.lang.Boolean]
    
    converted <==> jfx
    
    converted() shouldEqual true
    
    jfx setValue false
    
    converted() shouldEqual false
    
    converted() = true
    
    jfx.getValue shouldEqual true
  }
}