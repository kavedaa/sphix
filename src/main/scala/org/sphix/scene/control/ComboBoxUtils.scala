package org.sphix.scene.control

import javafx.scene.control._
import javafx.util.Callback

trait ComboBoxUtils[T] extends ComboBox[T] with ListCells[T] {

  /**
   * Utility method for setting a cell factory without bothering about
   * specifying it as a function of a ListView parameter.
   */
  def setCell(listCell: => ListCell[T]) {
    setCellFactory(new Callback[ListView[T], ListCell[T]] {
      def call(c: ListView[T]) = listCell
    })
  }

}