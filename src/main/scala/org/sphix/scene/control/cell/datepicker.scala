package org.sphix.scene.control.cell

import javafx.scene.control.TableCell
import java.time.LocalDate
import javafx.scene.control.DatePicker
import org.sphix.Val._
import javafx.beans.value.WritableValue
import java.time.format.DateTimeFormatter
import javafx.event.EventHandler
import javafx.scene.input._

trait DatePickerTableCell[S] extends TableCell[S, Option[LocalDate]] { cell =>

  def formatter: DateTimeFormatter

  this.getStyleClass add "datepicker-table-cell"

  private lazy val datePicker = new DatePicker {

    valueProperty onValue { date =>
      commitEdit(Option(date))
    }

    //	TODO this doesn't seem to work
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

      datePicker setValue getItem.orNull

      super.startEdit()

      setText(null)
      setGraphic(datePicker)

      datePicker requestFocus ()
    }
  }

  override def cancelEdit() {
    super.cancelEdit()
    setText(getItem map formatter.format getOrElse "")
    setGraphic(null)
  }

  override def updateItem(item: Option[LocalDate], empty: Boolean) {
    super.updateItem(item, empty)
    if (!empty) {
      if (isEditing) {
        datePicker setValue getItem.orNull
        setText(null)
        setGraphic(datePicker)
      }
      else {
        setText(getItem map formatter.format getOrElse "")
        setGraphic(null)
      }
    }
    else {
      setText(null)
      setGraphic(null)
    }
  }
}
