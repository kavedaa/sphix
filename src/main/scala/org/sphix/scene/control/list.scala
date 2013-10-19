package org.sphix.scene.control

import javafx.scene.{ control => jfxsc }
import javafx.util.Callback

class ListView[T] extends jfxsc.ListView[T] with ListCells[T] {

  /**
   * Utility method for setting a cell factory without bothering about
   * specifying it as a function of a ListView parameter.
   */
  def setCell(listCell: => jfxsc.ListCell[T]) {
    setCellFactory(new Callback[jfxsc.ListView[T], jfxsc.ListCell[T]] {
      def call(c: jfxsc.ListView[T]) = listCell
    })
  }

}