package org.sphix.scene.control.cell

import javafx.scene.control.TableCell
import javafx.beans.property.Property
import javafx.scene.control.CheckBox
import javafx.util.Callback
import javafx.scene.control.TableColumn
import javafx.beans.property.SimpleBooleanProperty
import javafx.geometry.Pos

class CheckBoxTableCell[S, T](f: S => Property[java.lang.Boolean]) extends TableCell[S, T] {

  lazy val checkbox = new CheckBox
  
  setAlignment(Pos.CENTER)

  var checked: Property[java.lang.Boolean] = new SimpleBooleanProperty

  override def updateItem(cellItem: T, empty: Boolean) {
    super.updateItem(cellItem, empty)
    if (!empty) {
      setGraphic(checkbox)
      checkbox.selectedProperty unbindBidirectional checked
      val item = getTableView.getItems get getIndex
      checked = f(item)
      checkbox.selectedProperty bindBidirectional checked
    }
  }
}

object CheckBoxTableCell {
  def apply[S, T](f: S => Property[java.lang.Boolean]) = new Callback[TableColumn[S, T], TableCell[S, T]] {
    def call(c: TableColumn[S, T]) = new CheckBoxTableCell(f)
  }
}
