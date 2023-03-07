package org.sphix.collection.transformation

// trait UnmodifiableObservableListWrapper[A]
//   extends java.util.AbstractList[A]
//   with javafx.collections.ObservableList[A] {

//   val msg = "Trying to modify unmodifiable list."
  
//   def addAll(elements: A*) = throw new UnsupportedOperationException(msg)  
//   def remove(from: Int, to: Int) = throw new UnsupportedOperationException(msg)
//   def removeAll(elements: A*) = throw new UnsupportedOperationException(msg)
//   def retainAll(elements: A*) = throw new UnsupportedOperationException(msg)
//   def setAll(elements: A*) = throw new UnsupportedOperationException(msg)
//   def setAll(col: java.util.Collection[_ <: A]) = throw new UnsupportedOperationException(msg)  
// }