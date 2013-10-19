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

class ComboBoxTableCell[S, T](items: ObservableSeq[T], converter: RightConverter[T, String]) extends TableCell[S, T] {

  this.getStyleClass().add("combo-box-table-cell");

  private lazy val comboBox = new ComboBox(items) {
    setMaxWidth(java.lang.Double.MAX_VALUE);

    getSelectionModel.selectedItemProperty onValue println
    
    setOnHiding { () =>
      println("hiding")
    }

    setOnHidden { () =>
      println("hidden")
      if (isEditing) {
        commitEdit(getSelectionModel.getSelectedItem)
      }
    }
  }

  override def startEdit() {
    if (isEditable && getTableView.isEditable && getTableColumn.isEditable) {

      comboBox.getSelectionModel select getItem

      super.startEdit()
      setText(null)
      setGraphic(comboBox)
      
     comboBox requestFocus()
      comboBox show()
    }

  }

  override def cancelEdit() {
    super.cancelEdit()
    setText(converter convert getItem)
    setGraphic(null)
  }

  override def updateItem(item: T, empty: Boolean) {
    super.updateItem(item, empty)
    if (isEmpty) {
      setText(null)
      setGraphic(null)
    }
    else {
      if (isEditing) {
        println("updateItem isEditing")
//        comboBox.getSelectionModel select getItem
        setText(null)
        setGraphic(comboBox)
      }
      else {
        setText(converter convert getItem)
        setGraphic(null)
      }
    }
  }
}
