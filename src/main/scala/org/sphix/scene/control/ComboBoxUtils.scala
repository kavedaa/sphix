package org.sphix.scene.control

import javafx.scene.control._
import javafx.util._

trait ComboBoxUtils[T] extends ComboBox[T] with ListCells[T] {

  /**
   * Utility method for setting a cell factory without bothering about
   * specifying it as a function of a ListView parameter.
   */
  def setCell(listCell: => ListCell[T]) = {
    setCellFactory(new Callback[ListView[T], ListCell[T]] {
      def call(c: ListView[T]) = listCell
    })
  }

}

trait StringComboBoxUtils[T] extends ComboBoxUtils[T] with ListCells[T] {

  def stringValue(x: T): String

  setCell(TextCell(stringValue))
  setButtonCell(TextCell(stringValue))
  setConverter {
    new StringConverter[T] {
      def fromString(x: String) = ???
      def toString(x: T) = stringValue(x)
    }
  }
}
