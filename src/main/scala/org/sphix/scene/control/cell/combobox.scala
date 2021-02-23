package org.sphix.scene.control.cell

import javafx.scene.control.TableCell
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.control.TableCell
import javafx.scene.control.TableColumn
import javafx.util.Callback
import javafx.util.StringConverter
import org.sphix.collection.mutable.ObservableBuffer
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import org.sphix.collection.ObservableSeq
import org.sphix.util.RightConverter
import org.sphix.util._
import org.sphix.Val._
import javafx.scene.input._
import org.sphix.scene.control.ComboBoxUtils
import javafx.scene.control.ListCell

class CellProxy[T] extends javafx.scene.control.Cell[T] {
  def proxyUpdateItem(item: T, empty: Boolean) {
    updateItem(item, empty)
  }
}

abstract class ComboBoxTableCell[S, T] extends TableCell[S, T] { cell =>

  def items(s: S): ObservableSeq[T]
  
  def f(t: T): String
  
  this.getStyleClass().add("combo-box-table-cell");

  private lazy val comboBox = new ComboBox[T] with ComboBoxUtils[T] {

    setCell(TextCell(f))
    setButtonCell(TextCell(f))

    setMaxWidth(java.lang.Double.MAX_VALUE);

    getSelectionModel.selectedItemProperty onValue { v =>
      Option(v) foreach { item =>
        commitEdit(item)
      }
    }

    setOnKeyPressed(new EventHandler[KeyEvent] {
      def handle(t: KeyEvent) {
        if (new KeyCodeCombination(KeyCode.ESCAPE) `match` t) {
          cancelEdit()
        }
      }
    })

  }

  override def startEdit() {
    if (isEditable && getTableView.isEditable && getTableColumn.isEditable) {
    
      comboBox setItems items(getTableView.getItems get getIndex)
      comboBox.getSelectionModel select getItem //	important that this comes before super.startEdit()

      super.startEdit()

      setText(null)
      setGraphic(comboBox)

      //      comboBox show ()	//	this is wanted for good UX but triggers weird bug 
      comboBox.requestFocus()
    }
  }

  override def cancelEdit() {
    super.cancelEdit()
    setText(f(getItem))
    setGraphic(null)
  }

  override def updateItem(item: T, empty: Boolean) {
    super.updateItem(item, empty)
    if (!empty) {
      if (isEditing) {
        comboBox.getSelectionModel select getItem
        setText(null)
        setGraphic(comboBox)
      }
      else {
        setText(f(getItem))
        setGraphic(null)
      }
    }
    else {
      setText(null)
      setGraphic(null)
    }
  }
}
