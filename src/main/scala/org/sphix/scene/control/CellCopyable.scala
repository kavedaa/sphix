package org.sphix.scene.control

import org.sphix.collection.mutable.ObservableBuffer._
import javafx.scene.input._
import javafx.scene.control._
import org.sphix.util._
import javafx.util.Callback

trait CellCopyable { this: TableView[_] =>

  getSelectionModel setSelectionMode SelectionMode.MULTIPLE
  getSelectionModel setCellSelectionEnabled true

  setOnKeyReleased { (e: KeyEvent) =>
    val keyCombination = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN)
    if (keyCombination `match` e) {
      copyTableSelectionToClipboard()
    }
  }

  /**
   *  Copies the text in all selected cells in tab-separated format to the clipboard.
   */
  def copyTableSelectionToClipboard() = {
    val cells = getSelectionModel.getSelectedCells
    val rows = cells groupBy (_.getRow)
    val matrix = rows.toSeq sortBy (_._1) map (_._2 sortBy (_.getColumn))
    val data = matrix map { row =>
      row map { tp =>
        val columns = getColumns
        val dataColumns = linearColumns(columns)
        val visibleColumns = dataColumns filter (_.isVisible)
        val column = visibleColumns(tp.getColumn)
        val data = column getCellData tp.getRow
        val factory = tp.getTableColumn.getCellFactory.asInstanceOf[Callback[TableColumn[_, _], TableCell[_, _]]]
        val cell = factory call tp.getTableColumn
        cell match {
          case c: org.sphix.scene.control.cell.Cell[_] =>
            c.asInstanceOf[org.sphix.scene.control.cell.Cell[Any]] onUpdate data
            val text = c.getText
            if (text == null) {
              c.getGraphic match {
                case l: Labeled => l.getText
                case _          => ""
              }
            }
            else text
          case _ =>
            data.toString
        }
      } mkString "\t"
    } mkString "\n"
    val clipboardContent = new ClipboardContent
    clipboardContent putString data
    Clipboard.getSystemClipboard setContent clipboardContent
  }

  private def linearColumns(columns: Seq[TableColumn[_, _]]) =
    columns flatMap childColumns

  private def childColumns(column: TableColumn[_, _]): Seq[TableColumn[_, _]] =
    column.getColumns match {
      case xs if xs.isEmpty => Seq(column)
      case xs               => xs flatMap childColumns
    }

}