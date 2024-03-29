package org.sphix.scene.control.cell

import javafx.scene.control.TextField
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import javafx.util.Callback
import javafx.beans.InvalidationListener
import javafx.beans.Observable
import javafx.beans.property.Property
import javafx.event.EventHandler
import javafx.scene.input.KeyEvent
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.control.TableCell
import org.sphix.util.RightConverter
import javafx.scene.control.TableColumn
import org.sphix.util.DefaultConverter

trait TextFieldTableCell[S, T] extends TableCell[S, T] { cell =>

  def converter: RightConverter[T, String]

  lazy val textField = new TextField {

    setOnKeyPressed(new EventHandler[KeyEvent] {
      def handle(t: KeyEvent) = {
        t match {
          case t if new KeyCodeCombination(KeyCode.ENTER) `match` t =>
            converter deconvert getText map commitEdit
          case t if new KeyCodeCombination(KeyCode.ESCAPE) `match` t => cell.cancelEdit()
          case _ =>
        }
      }
    })
  }

  override def startEdit() = {
    if (isEditable && getTableView.isEditable) {
      super.startEdit()
      textField setText (converter convert getItem)
      setText(null)
      setGraphic(textField)
      textField.requestFocus()
    }
  }

  override def commitEdit(value: T) = {
    super.commitEdit(value)
    setGraphic(null)
    getTableView.requestFocus()
  }

  override def cancelEdit() = {
    super.cancelEdit()
    setText(converter convert getItem)
    setGraphic(null)
  }

  override def updateItem(item: T, empty: Boolean) = {
    super.updateItem(item, empty)
    if (isEmpty()) {
      setText(null)
      setGraphic(null)
    }
    else {
      if (isEditing()) {
        if (textField != null) {
          textField setText (converter convert getItem)
        }
        setText(null)
        setGraphic(textField)
      }
      else {
        setText(converter convert getItem)
        setGraphic(null)
      }
    }
  }
}

object TextFieldTableCell {
  def apply[S, T](converter0: RightConverter[T, String] = new DefaultConverter[String]) =
    new Callback[TableColumn[S, T], TableCell[S, T]] {
      def call(c: TableColumn[S, T]) = new TextFieldTableCell[S, T] {
        def converter = converter0
      }
    }
}


trait TextFieldListCell[T] extends ListCell[T] { cell =>

  def converter: RightConverter[T, String]

  lazy val textField = new TextField {

    setOnKeyPressed(new EventHandler[KeyEvent] {
      def handle(t: KeyEvent) = {
        t match {
          case t if new KeyCodeCombination(KeyCode.ENTER) `match` t =>
            converter deconvert getText map commitEdit
          case t if new KeyCodeCombination(KeyCode.ESCAPE) `match` t => cell.cancelEdit()
          case _ =>
        }
      }
    })
  }

  override def startEdit() = {
    if (isEditable && getListView.isEditable) {
      super.startEdit()
      textField setText (converter convert getItem)
      setText(null)
      setGraphic(textField)
      textField.requestFocus()
    }
  }

  override def commitEdit(value: T) = {
    super.commitEdit(value)
    setGraphic(null)
    getListView.requestFocus()
  }

  override def cancelEdit() = {
    super.cancelEdit()
    setText(converter convert getItem)
    setGraphic(null)
  }

  override def updateItem(item: T, empty: Boolean) = {
    super.updateItem(item, empty)
    if (isEmpty()) {
      setText(null)
      setGraphic(null)
    }
    else {
      if (isEditing()) {
        if (textField != null) {
          textField setText (converter convert getItem)
        }
        setText(null)
        setGraphic(textField)
      }
      else {
        setText(converter convert getItem)
        setGraphic(null)
      }
    }
  }
}



//class TextFieldPropertyListCell extends ListCell[Property[String]] with TextFieldPropertyCell
//
//object TextFieldPropertyListCell extends Callback[ListView[Property[String]], ListCell[Property[String]]] {
//  def call(v: ListView[Property[String]]) = new TextFieldPropertyListCell
//}
//
