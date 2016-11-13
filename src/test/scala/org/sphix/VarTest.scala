package org.sphix

import org.scalatest._
import javafx.beans.InvalidationListener
import javafx.beans.value.ChangeListener
import javafx.beans.property.SimpleObjectProperty
import org.sphix.util.FullConverter
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.ReadOnlyStringWrapper

class VarTest extends FeatureSpec with Matchers {

  feature("factory") {

    scenario("simple") {

      val p = Var(3)

      p() should equal(3)
    }
  }

  feature("set value") {

    scenario("when not bound") {

      val p = Var(3)

      p() = 4

      p() should equal(4)
    }

    scenario("when bound") {

      val p = Var(3)
      val q = Var(4)

      p <== q

      evaluating { p() = 5 } should produce[RuntimeException]

      p() should equal(4)
    }

    scenario("when same value - nothing should fire") {

      var count = 0

      val p = Var(3)

      p observe { count += 1 }

      p() = 4

      count should equal(1)

      p() = 4

      count should equal(1)
    }
  }

  feature("binding") {

    scenario("bind") {

      var prev = 0
      var curr = 0

      val p = Var(3)
      val q = Var(4)

      p onChange { (ov, prev0, curr0) =>
        prev = prev0
        curr = curr0
      }

      p <== q

      p() should equal(4)
      p.isBound should be(true)
      prev shouldEqual 3
      curr shouldEqual 4
      
      q() = 5

      p() should equal(5)
      prev shouldEqual 4
      curr shouldEqual 5
    }

    scenario("unbind") {

      val p = Var(3)
      val q = Var(4)

      p <== q

      p() should equal(4)

      p unbind ()

      q() = 5

      p() shouldEqual 4
    }

    scenario("chained bind") {

      val p1 = Var(3)
      val p2 = Var(4)
      val p3 = Var(5)

      p1 <== p2

      p1() should equal(4)

      p2 <== p3

      p2() should equal(5)
      p1() should equal(5)

      p3() = 6

      p2() should equal(6)
      p1() should equal(6)
    }

    scenario("bind to JFX property") {

      var prev = ""
      var curr = ""
      
      val p = Var("Hello")
      val q = new SimpleStringProperty("Goodbye")

      p onChange { (ov, prev0, curr0) =>
        prev = prev0
        curr = curr0
      }

      p <== q

      p() shouldEqual "Goodbye"
      prev shouldEqual "Hello"
      curr shouldEqual "Goodbye"

      q setValue "Hello again"

      p() shouldEqual "Hello again"
      prev shouldEqual "Goodbye"
      curr shouldEqual "Hello again"
    }

    scenario("bind to read-only JFX property") {

      val p = Var("Hello")
      val q = new ReadOnlyStringWrapper("Goodbye")
      val q1 = q.getReadOnlyProperty

      p <== q1

      p() shouldEqual "Goodbye"

      q setValue "Hello again"

      p() shouldEqual "Hello again"

    }

    scenario("pimp JFX property") {

      val p = new SimpleObjectProperty[Int](3)
      val q = Var(4)

      import Var._

      p <== q

      p() should equal(4)

      q() = 5

      p() should equal(5)
    }
  }

  feature("bidirectional binding") {

    scenario("bind - without converter") {

      val p = Var(3)
      val q = Var(4)

      p <==> q

      p() shouldEqual 4

      p() = 5

      q() shouldEqual 5

      q() = 6

      p() shouldEqual 6
    }

    scenario("unbind - without converter") {

      val p = Var(3)
      val q = Var(4)

      p <==> q

      p() shouldEqual 4

      p <=!=> q

      p() = 5

      q() shouldEqual 4
    }

    scenario("bind - with converter") {

      val p = Var(3)
      val q = Var(3)

      import Var._

      p <=~ FullConverter[Int, Int](_ + 2, _ - 2) ~=> q

      p() shouldEqual 1

      p() = 2

      q() shouldEqual 4

      q() = 5

      p() shouldEqual 3
    }

    scenario("unbind - with converter") {

      val p = Var(3)
      val q = Var(3)

      import Var._

      p <=~ FullConverter[Int, Int](_ + 2, _ - 2) ~=> q

      p() shouldEqual 1

      p <=!=> q

      q() = 2

      p() shouldEqual 1
    }

    scenario("conversion between primitive types") {

      val p = Var[Boolean](true)
      val q = new SimpleBooleanProperty(false)

      p <=~=> q

      p() shouldEqual false

      p() = true

      q.get shouldEqual true

      q set false

      p() shouldEqual false
    }

  }

  feature("misc") {

    scenario("variance") {

      val v1 = Var[Option[Int]](None)

      // must ascribe super type for binding (et al?) to work
      val v2: Val[Option[Int]] = Val(Some(3))

      v1 <== v2
    }

  }

}