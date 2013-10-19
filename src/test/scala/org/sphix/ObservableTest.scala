package org.sphix

import org.scalatest.FeatureSpec
import org.scalatest.matchers.ShouldMatchers
import javafx.beans.InvalidationListener
import javafx.{ beans => jfxb }
import javafx.beans.property.SimpleStringProperty

class ObservableTest extends FeatureSpec with ShouldMatchers {

  //	Using a Var to test all features of Observable

  feature("invalidation listener") {

    scenario("single") {

      var evidence: jfxb.Observable = null
      var count = 0

      val o = Var(3)

      val listener1, listener2 = new InvalidationListener {
        def invalidated(o: javafx.beans.Observable) {
          evidence = o
          count += 1
        }
      }

      o addListener listener1
      o addListener listener2

      o() = 4

      evidence should equal(o)
      count should equal(2)

      o removeListener listener1
      o() = 5
      count should equal(3)

      o removeListener listener2
      o() = 6
      count should equal(3)
    }
    
    scenario("tuple") {

      var evidence: jfxb.Observable = null
      var count = 0

      val o1, o2 = Var(3)

      val listener1, listener2 = new InvalidationListener {
        def invalidated(o: javafx.beans.Observable) {
          evidence = o
          count += 1
        }
      }

      (o1, o2) addListener listener1
      (o1, o2) addListener listener2

      o1() = 4
      
      evidence should equal(o1)
      count should equal(2)

      o2() = 4
      
      evidence should equal(o2)
      count should equal(4)
      
      (o1, o2) removeListener listener1
      o1() = 5
      count should equal(5)

      (o1, o2) removeListener listener2
      o2() = 6
      count should equal(5)
    }

    scenario("seq") {
      
      var evidence: jfxb.Observable = null
      var count = 0

      val o1, o2 = Var(3)

      val os = new SeqObservable(Seq(o1, o2))
      
      val listener1, listener2 = new InvalidationListener {
        def invalidated(o: javafx.beans.Observable) {
          evidence = o
          count += 1
        }
      }

      os addListener listener1
      os addListener listener2

      o1() = 4
      
      evidence should equal(o1)
      count should equal(2)

      o2() = 4
      
      evidence should equal(o2)
      count should equal(4)
      
      os removeListener listener1
      o1() = 5
      count should equal(5)

      os removeListener listener2
      o2() = 6
      count should equal(5)      
    }
  }

  feature("onInvalidate") {

    scenario("single") {

      var evidence: jfxb.Observable = null
      var count = 0

      val o = Var(3)

      val obs = o onInvalidate { o =>
        evidence = o
        count += 1
      }

      o() = 4
      evidence should equal(o)
      count should equal(1)

      obs dispose ()
      o() = 5
      count should equal(1)
    }

    scenario("tuple") {

      var evidence: jfxb.Observable = null
      var count = 0

      val o1, o2 = Var(3)

      val obs = (o1, o2) onInvalidate { o =>
        evidence = o
        count += 1
      }

      o1() = 4
      evidence should equal(o1)
      count should equal(1)

      o2() = 4
      evidence should equal(o2)
      count should equal(2)

      obs dispose ()

      o1() = 5
      count should equal(2)

      o2() = 5
      count should equal(2)
    }
  }

  feature("onInvalidateOnce") {

    scenario("single") {

      var evidence: jfxb.Observable = null
      var count = 0

      val o = Var(3)

      o onInvalidateOnce { o =>
        evidence = o
        count += 1
      }

      o() = 4
      evidence should equal(o)
      count should equal(1)

      o() = 5
      count should equal(1)
    }
    
    scenario("tuple") {
      
      var evidence: jfxb.Observable = null
      var count = 0

      val o1, o2 = Var(3)

      (o1, o2) onInvalidateOnce { o =>
        evidence = o
        count += 1
      }

      o1() = 4
      evidence should equal(o1)
      count should equal(1)

      o2() = 5
      count should equal(1)      
    }
  }

  feature("observe") {

    scenario("single") {

      val o = Var(3)
      var count = 0

      val obs = o observe {
        count += 1
      }

      o() = 4
      count should equal(1)

      obs dispose ()
      o() = 5
      count should equal(1)

    }

    scenario("tuple") {

      val o1, o2 = Var(3)
      var count = 0

      val obs = (o1, o2) observe {
        count += 1
      }

      o1() = 4
      count should equal(1)

      o2() = 4
      count should equal(2)

      obs dispose ()

      o1() = 5
      count should equal(2)

      o2() = 5
      count should equal(2)

    }
  }

  feature("observeOnce") {

    scenario("single") {

      val o = Var(3)
      var count = 0

      o observeOnce {
        count += 1
      }

      o() = 4
      count should equal(1)

      o() = 5
      count should equal(1)
    }
    
    scenario("tuple") {
      
      val o1, o2 = Var(3)
      var count = 0

      (o1, o2) observeOnce {
        count += 1
      }

      o1() = 4
      count should equal(1)

      o2() = 5
      count should equal(1)      
    }
  }

  feature("apply") {

    scenario("single") {

      val o = Var(3)

      val v = o(_())
      v() should equal(3)

      o() = 4
      v() should equal(4)
    }

    scenario("tuple") {

      val o1, o2 = Var(3)

      val v = (o1, o2)(_() + _())

      v() should equal(6)

      o1() = 4
      v() should equal(7)

      o2() = 4
      v() should equal(8)
    }
  }

  feature("implicit conversion from jfx") {

    scenario("single") {

      var count = 0

      val p = new SimpleStringProperty("hello")

      val obs = p observe {
        count += 1
      }

      p setValue "goodbye"
      count should equal(1)

      obs dispose ()

      p setValue "hello again"
      count should equal(1)
    }

    scenario("tuple") {

      var count = 0

      val p = new SimpleStringProperty("hello")
      val q = new SimpleStringProperty("hi")

      val obs = (p, q) observe {
        count += 1
      }

      p setValue "goodbye"
      q setValue "bye"
      count should equal(2)

      obs dispose ()

      p setValue "hello again"
      q setValue "hi again"
      count should equal(2)
    }
  }

}

