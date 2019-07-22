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

trait TriStateCheckBoxTableCell[S, T] extends TableCell[S, T] {

  def checked(s: S): Property[Boolean]
  def indeterminate(s: S): Property[Boolean]
  
  lazy val checkbox = new CheckBox {
    setAllowIndeterminate(true)
  }  
  
  setAlignment(Pos.CENTER)

  var dataChecked = Var[java.lang.Boolean](false)
  var dataIndeterminate = Var[java.lang.Boolean](false)

  override def updateItem(cellItem: T, empty: Boolean) {
    super.updateItem(cellItem, empty)
    if (!empty) {
      setGraphic(checkbox)
      checkbox.selectedProperty unbindBidirectional dataChecked
      checkbox.indeterminateProperty unbindBidirectional dataIndeterminate
      val rowItem = getTableView.getItems get getIndex
      dataChecked = checked(rowItem).convert[java.lang.Boolean]
      dataIndeterminate = indeterminate(rowItem).convert[java.lang.Boolean]
      checkbox.selectedProperty bindBidirectional dataChecked
      checkbox.indeterminateProperty bindBidirectional dataIndeterminate
    }
    else {
      setGraphic(null)
    }
  }
}

object TriStateCheckBoxTableCell {
  def apply[S, T](checked0: S => Property[Boolean], indeterminate0: S => Property[Boolean]) = new Callback[TableColumn[S, T], TableCell[S, T]] {
    def call(c: TableColumn[S, T]) = new TriStateCheckBoxTableCell[S, T] {
      def checked(s: S) = checked0(s)
      def indeterminate(s: S) = indeterminate0(s)
    }
  }
}
