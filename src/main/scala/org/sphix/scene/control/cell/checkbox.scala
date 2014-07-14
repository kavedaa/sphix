package org.sphix.scene.control.cell

import javafx.scene.control.TableCell
import javafx.beans.property.Property
import javafx.scene.control.CheckBox
import javafx.util.Callback
import javafx.scene.control.TableColumn
import javafx.beans.property.SimpleBooleanProperty
import javafx.geometry.Pos
import org.sphix.Var
import Var._

trait CheckBoxTableCell[S, T] extends TableCell[S, T] {

  def f(s: S): Property[Boolean]
  
  lazy val checkbox = new CheckBox
  lazy val checked = checkbox.selectedProperty
  
  setAlignment(Pos.CENTER)

  var data = Var[java.lang.Boolean](false)

  override def updateItem(cellItem: T, empty: Boolean) {
    super.updateItem(cellItem, empty)
    if (!empty) {
      setGraphic(checkbox)
      checked unbindBidirectional data 
      val rowItem = getTableView.getItems get getIndex
      data = f(rowItem).convert[java.lang.Boolean]
      checked bindBidirectional data
    }
    else {
      setGraphic(null)
    }
  }
}

object CheckBoxTableCell {
  def apply[S, T](f0: S => Property[Boolean]) = new Callback[TableColumn[S, T], TableCell[S, T]] {
    def call(c: TableColumn[S, T]) = new CheckBoxTableCell[S, T] {
      def f(s: S) = f0(s)
    }
  }
}
