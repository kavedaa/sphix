package org.sphix.collection.mutable

import org.scalatest._
import javafx.beans.InvalidationListener
import javafx.beans.Observable
import org.sphix.collection.Change
import Change._
import javafx.collections.FXCollections
import org.sphix._
import org.sphix.collection.SeqFunc

class ObservableBufferTest extends FunSuite with Matchers {

  test("factory methods") {

    ObservableBuffer().toList shouldBe Nil

    ObservableBuffer('A, 'B).toList shouldEqual List('A, 'B)

    ObservableBuffer('A, 'B).to[List] shouldEqual List('A, 'B)

    List('A, 'B).to[ObservableBuffer] shouldEqual ObservableBuffer('A, 'B)

    ObservableBuffer('A, 'B) shouldEqual ObservableBuffer('A, 'B) 
  }

  test("apply") {

    val ob = ObservableBuffer('A, 'B)

    ob(0) shouldEqual 'A
    ob(1) shouldEqual 'B
  }

  test("length") {
    ObservableBuffer().length shouldEqual 0
    ObservableBuffer('A, 'B).length shouldEqual 2
  }

  test("clear") {

    var changes: Seq[Change[Symbol]] = Nil
    var count = 0

    val ob = ObservableBuffer('A, 'B)

    ob onChange { cs =>
      changes = cs
      count += 1
    }

    ob clear ()

    count shouldEqual 1
    changes shouldEqual Seq(Removed(0, Seq('A, 'B)))
    ob shouldEqual Nil 
  }

  test("insert single") {

    var changes: Seq[Change[Symbol]] = Nil
    var count = 0

    val ob = ObservableBuffer('A)

    ob onChange { cs =>
      changes = cs
      count += 1
    }

    ob += 'B
    count shouldEqual 1
    changes shouldEqual Seq(Added(1, Seq('B)))
    ob shouldEqual ObservableBuffer('A, 'B)

    count = 0

    'C +=: ob
    count shouldEqual 1
    changes shouldEqual Seq(Added(0, Seq('C)))
    ob shouldEqual ObservableBuffer('C, 'A, 'B)
  }

  test("insert multiple") {

    var changes: Seq[Change[Symbol]] = Nil
    var count = 0

    val init = Seq('A, 'B)

    val ob = init.to[ObservableBuffer]

    ob onChange { cs =>
      changes = cs
      count += 1
    }

    ob insertAll (1, List('C, 'D))
    count shouldEqual 1
    changes shouldEqual Seq(Added(1, Seq('C, 'D)))
    ob shouldEqual ObservableBuffer('A, 'C, 'D, 'B)

    ob clear ()
    ob insertAll (0, init)
    count = 0

    ob ++= List('C, 'D)
    count shouldEqual 1
    changes shouldEqual Seq(Added(2, Seq('C, 'D)))
    ob shouldEqual ObservableBuffer('A, 'B, 'C, 'D)

    ob clear ()
    ob insertAll (0, init)
    count = 0

    ob appendAll (List('C, 'D))
    count shouldEqual 1
    changes shouldEqual Seq(Added(2, Seq('C, 'D)))
    ob shouldEqual ObservableBuffer('A, 'B, 'C, 'D)

    ob clear ()
    ob insertAll (0, init)
    count = 0

    List('C, 'D) ++=: ob
    count shouldEqual 1
    changes shouldEqual Seq(Added(0, Seq('C, 'D)))
    ob shouldEqual ObservableBuffer('C, 'D, 'A, 'B)

    ob clear ()
    ob insertAll (0, init)
    count = 0

    ob prependAll List('C, 'D)
    count shouldEqual 1
    changes shouldEqual Seq(Added(0, Seq('C, 'D)))
    ob shouldEqual ObservableBuffer('C, 'D, 'A, 'B)

    ob clear ()
    ob insertAll (0, init)
    count = 0

    ob += ('C, 'D, 'E, 'F)
    count shouldEqual 1
    changes shouldEqual Seq(Added(2, Seq('C, 'D, 'E, 'F)))
    ob shouldEqual ObservableBuffer('A, 'B, 'C, 'D, 'E, 'F)

    ob clear ()
    ob insertAll (0, init)
    count = 0

    ob append ('C, 'D, 'E)
    count shouldEqual 1
    changes shouldEqual Seq(Added(2, Seq('C, 'D, 'E)))
    ob shouldEqual ObservableBuffer('A, 'B, 'C, 'D, 'E)

    ob clear ()
    ob insertAll (0, init)
    count = 0

    ob insert (1, 'C, 'D)
    count shouldEqual 1
    changes shouldEqual Seq(Added(1, Seq('C, 'D)))
    ob shouldEqual ObservableBuffer('A, 'C, 'D, 'B)

    ob clear ()
    ob insertAll (0, init)
    count = 0

    ob prepend ('C, 'D, 'E)
    count shouldEqual 1
    changes shouldEqual Seq(Added(0, Seq('C, 'D, 'E)))
    ob shouldEqual ObservableBuffer('C, 'D, 'E, 'A, 'B)
  }

  test("remove single") {

    var changes: Seq[Change[Symbol]] = Nil
    var count = 0

    val init = Seq('A, 'B, 'C)

    val ob = init.to[ObservableBuffer]

    ob onChange { cs =>
      changes = cs
      count += 1
    }

    ob remove 1
    count shouldEqual 1
    changes shouldEqual Seq(Removed(1, Seq('B)))
    ob shouldEqual ObservableBuffer('A, 'C)

    ob clear ()
    ob insertAll (0, init)
    count = 0

    ob -= 'B
    count shouldEqual 1
    changes shouldEqual Seq(Removed(1, Seq('B)))
    ob shouldEqual ObservableBuffer('A, 'C) 
  }

  test("remove multiple") {

    var changes: Seq[Change[Symbol]] = Nil
    var count = 0

    val init = Seq('A, 'B, 'C, 'D, 'E)

    val ob = init.to[ObservableBuffer]

    ob onChange { cs =>
      changes = cs
      count += 1
    }

    ob --= List('B, 'C)
    count shouldEqual 1
    changes shouldEqual Seq(Removed(1, Seq('B, 'C)))
    ob shouldEqual ObservableBuffer('A, 'D, 'E)

    ob clear ()
    ob insertAll (0, init)
    count = 0

    ob --= List('A, 'C)
    count shouldEqual 1
    changes shouldEqual Seq(Removed(0, Seq('A)), Removed(1, Seq('C)))
    ob shouldEqual ObservableBuffer('B, 'D, 'E)

    ob clear ()
    ob insertAll (0, init)
    count = 0

    ob -= ('A, 'C, 'D)
    count shouldEqual 1
    changes shouldEqual Seq(Removed(0, Seq('A)), Removed(1, Seq('C, 'D)))
    ob shouldEqual ObservableBuffer('B, 'E)

    ob clear ()
    ob insertAll (0, init)
    count = 0

    ob remove (1, 3)
    count shouldEqual 1
    changes shouldEqual Seq(Removed(1, Seq('B, 'C, 'D)))
    ob shouldEqual ObservableBuffer('A, 'E)

    ob clear ()
    ob insertAll (0, init)
    count = 0

    ob trimStart 2
    count shouldEqual 1
    changes shouldEqual Seq(Removed(0, Seq('A, 'B)))
    ob shouldEqual ObservableBuffer('C, 'D, 'E)

    ob clear ()
    ob insertAll (0, init)
    count = 0

    ob trimEnd 2
    count shouldEqual 1
    changes shouldEqual Seq(Removed(3, Seq('D, 'E)))
    ob shouldEqual ObservableBuffer('A, 'B, 'C)
  }

  test("update at n") {

    var changes: Seq[Change[Symbol]] = Nil
    var count = 0

    val init = Seq('A, 'B, 'C)

    val ob = init.to[ObservableBuffer]

    ob onChange { cs =>
      changes = cs
      count += 1
    }

    ob(1) = 'D

    count shouldEqual 1
    
    //	I don't know why this gives add, remove, instead simply an update
    //	This comes from the JavaFX OList implementation
    changes shouldEqual Seq(Added(1, Seq('D)), Removed(1, Seq('B)))
    
    ob shouldEqual ObservableBuffer('A, 'D, 'C)
  }

  test("update entire buffer") {
    
    var changes: Seq[Change[Symbol]] = Nil
    var count = 0

    val init = Seq('A, 'B, 'C)

    val ob = init.to[ObservableBuffer]

    val other = Seq('D, 'E, 'F)
    
    ob onChange { cs =>
      changes = cs
      count += 1
    }

    ob() = other

    count shouldEqual 1
    
    //	Not sure why the changes are reported in seemingly reverse order
    //	Again, from the JavaFX OList implementation
    changes shouldEqual Seq(Added(0, Seq('D, 'E, 'F)), Removed(0, Seq('A, 'B, 'C)))
    
    ob shouldEqual ObservableBuffer('D, 'E, 'F)    
  }
  
  test("bind") {

    //  screw this
    
//    var changes: Seq[Change[Int]] = Nil
//    var count = 0
//
//    val ob1 = ObservableBuffer(1, 2, 3)
//    
//    val sf = SeqFunc(ob1) { x => x }
//    
//    val ob2 = ObservableBuffer[Int]()    
//   
//    ob2 onChange { cs =>
//      changes = cs
//      count += 1
//    }
//    
//    ob2 <== sf
//
//    count shouldEqual 1
//    ob2 shouldEqual Seq(1, 2, 3)    
//    
//    ob1() = Seq(7, 8, 9)
//    
//    count shouldEqual 2
//    ob2 shouldEqual Seq(7, 8, 9)
//    
//    ob1 += 4
//    
//    count shouldEqual 3
//    ob2 shouldEqual Seq(7, 8, 9, 4)
//    
//    ob1 -= 8
//
//    count shouldEqual 4
//    ob2 shouldEqual Seq(7, 9, 4)
//    
//    ob1 doSortWith(_ > _)
//    
//    count shouldEqual 5
//    ob2 shouldEqual Seq(9, 7, 4)
//
//    ob1(1) = 5
//    
//    count shouldEqual 6
//    ob2(1) shouldEqual 5
//    
//    
//    //	TODO: unbind
//    ob2 unbind()
  }
  
  test("element change") {
    
    var changes: Seq[Change[Val[Int]]] = Nil
    var count = 0

    val ob = ObservableBuffer[Val[Int]]((x: Val[Int]) => x)

    val v1 = Var(1)
    
    ob += v1
    
    ob onChange { cs =>
      changes = cs
      count += 1
    }

    v1() = 2
    
    count shouldEqual 1
    
    val v2 = Var(100)
    
    ob += v2
    
    count shouldEqual 2
    
    v2() = 101
    
    count shouldEqual 3
    
    ob -= v1

    count shouldEqual 4
    
    v1() = 3
    
    count shouldEqual 4
  }

  test("iteration with element change") {
    
    var count = 0

    val ob = ObservableBuffer[Var[Int]]((x: Var[Int]) => x)
    
    ob() = Seq(Var(1), Var(2), Var(3))
    
    ob onChange { cs =>
      count += 1
    }

    ob foreach(_ update 4)
    
    count shouldEqual 3
  }
  
  test("doSortBy") {

    val ob = ObservableBuffer(3, 1, 5, 2, 4)

    ob doSortBy (x => x)

    ob shouldEqual Seq(1, 2, 3, 4, 5)
  }
  
  test("doSortWith") {

    val ob = ObservableBuffer(3, 1, 5, 2, 4)

    ob doSortWith (_ > _)

    ob shouldEqual Seq(5, 4, 3, 2, 1)    
  }

  test("sort underlying OList") {

    val ob = ObservableBuffer(2, 3, 1)

    var changes: Seq[Change[Int]] = Nil
    var count = 0

    ob onChange { cs =>
      changes = cs
      count += 1
    }

    FXCollections sort (ob.toObservableList, implicitly[Ordering[Int]])

    count shouldEqual 1
    val Seq(Permutated(from, to, f)) = changes
    (from, to) shouldEqual (0, 3)
    f(0) shouldEqual 1
    f(1) shouldEqual 2
    f(2) shouldEqual 0
    ob shouldEqual ObservableBuffer(1, 2, 3)
  }
}