package org.sphix

import org.scalatest.FeatureSpec
import org.scalatest.matchers.ShouldMatchers
import org.sphix.collection.mutable.ObservableBuffer

class ApplierTest extends FeatureSpec with ShouldMatchers {

  //	these aren't real tests, we just check that the compiler is happy
  
  feature("default applier") {
    
    scenario("single") {
      
      val ob = ObservableBuffer(3, 4, 5)
      
      val v = ob { _.headOption }
    }
    
    scenario("tuple") {
      
      val ob1 = ObservableBuffer(3, 4, 5)
      
      val ob2 = ob1 filtered(_ > 4)
      
      val v = (ob1, ob2) { _.size + _.size }
    }
    
  }
}