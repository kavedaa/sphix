package org.sphix

import org.scalatest._
import javafx.beans.value.ChangeListener
import javafx.beans.property.SimpleIntegerProperty

class ValTest extends FeatureSpec with Matchers {

  type OV[A] = javafx.beans.value.ObservableValue[A]

  feature("factory") {

    scenario("apply") {

      val v = Val(3)

      v() should equal(3)
    }

    scenario("toVal") {

      import Val._

      val v = 3.toVal

      v shouldBe a[Val[_]]
      v() shouldEqual 3
    }

  }

  //	Using av Var to the features of Val

  feature("change listeners") {

    scenario("single") {

      var evidence: OV[_ <: Int] = null
      var count = 0

      val v = Var(3)

      val listener1, listener2 = new ChangeListener[Int] {
        def changed(ov: OV[_ <: Int], o: Int, n: Int) {
          evidence = ov
          count += 1
        }
      }

      v addListener listener1
      v addListener listener2

      v() = 4
      evidence should equal(v)
      count should equal(2)

      v removeListener listener1
      v() = 5
      evidence should equal(v)
      count should equal(3)

      v removeListener listener2
      v() = 6
      evidence should equal(v)
      count should equal(3)
    }
  }

  feature("onChange") {

    scenario("single") {

      var oldValue = 0
      var newValue = 0

      val v = Var(3)

      val obs = v onChange { (ov, o, n) =>
        oldValue = o
        newValue = n
      }

      v() = 4
      oldValue should equal(3)
      newValue should equal(4)

      obs dispose ()

      v() = 5
      newValue should equal(4)
    }
  }

  feature("onChangeOnce") {

    scenario("single") {

      var oldValue = 0
      var newValue = 0

      val v = Var(3)

      val obs = v onChangeOnce { (ov, o, n) =>
        oldValue = o
        newValue = n
      }

      v() = 4
      oldValue should equal(3)
      newValue should equal(4)

      v() = 5
      newValue should equal(4)
    }
  }

  feature("onValue") {

    scenario("single") {

      var value = 0

      val v = Var(3)

      val obs = v onValue { value = _ }

      v() = 4
      value should equal(4)

      obs dispose ()

      v() = 5
      value should equal(4)
    }
  }

  feature("onValueOnce") {

    scenario("single") {

      var value = 0

      val v = Var(3)

      val obs = v onValueOnce { value = _ }

      v() = 4
      value should equal(4)

      v() = 5
      value should equal(4)
    }
  }

  feature("map") {

    scenario("single") {

      val v = Var(3)

      val w = v map (_ + 1)

      w() should equal(4)

      v() = 5

      w() should equal(6)

    }

    scenario("bug hunt") {

      val v = Var("foo")

      val w: Val[Option[String]] = v map (x => Some(x))

      w()

      w() should equal(Some("foo"))
    }

  }

  feature("flatten") {

    scenario("single") {

      val v = Var(3)

      val w = Var(100)

      val x = Var(v)

      val z = x.flatten

      z() should equal(3)

      v() = 4

      z() should equal(4)

      x() = w

      z() should equal(100)

      w() = 101

      z() should equal(101)
    }
  }

  feature("filter") {

    scenario("single") {

      val v = Var(3)

      val w = v filter (_ < 10)

      w() should equal(Some(3))

      v() = 15

      w() should equal(None)
    }
  }

  feature("implicit conversion from jfx") {

    import Val._

    scenario("single val") {

      var value = 0

      val sip = new SimpleIntegerProperty(3)

      sip onValue { v => value = v.intValue }

      sip setValue 4

      value should equal(4)
    }

    scenario("tuple") {

      val sip1, sip2 = new SimpleIntegerProperty(3)

      val prod = (sip1, sip2) map (_.intValue + _.intValue)
    }
  }

  feature("pack") {

    scenario("tuple 2") {

      val a = Var(3)
      val b = Var(4)

      val packed = (a, b).pack

      packed() should equal((3, 4))

      a() = 5

      packed() should equal((5, 4))
    }
  }

  feature("experimental") {

    val a = Val(2)

    val b = Var(4)

    //    b <== a.!
    //    
    //    b <== -a
    //    
    //    b <== a.jfx
    //    
    //    b <== a.safe
    //    
    //    b <== a.~
    //    
    //    b := 3
    //    
    //    b <:== a
  }

}
