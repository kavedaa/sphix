package org.sphix.scene.control

import org.sphix.collection.mutable.ObservableBuffer._
import javafx.scene.input._
import javafx.scene.control._
import org.sphix.util._
import javafx.util.Callback

trait CellCopyable {
  this: TableView[_] =>

  getSelectionModel setSelectionMode SelectionMode.MULTIPLE
  getSelectionModel setCellSelectionEnabled true

  setOnKeyReleased { (e: KeyEvent) =>
    val keyCombination = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN)
    if (keyCombination `match` e) {
      copyTableSelectionToClipboard()
    }
  }

  /**
    * Copies the text in all selected cells in tab-separated format to the clipboard.
    */
  def copyTableSelectionToClipboard(): Unit = {
    val formatted = tableSelectionTextMatrix map (_ mkString "\t") mkString "\n"
    val clipboardContent = new ClipboardContent
    clipboardContent putString formatted
    Clipboard.getSystemClipboard setContent clipboardContent
  }

  /**
    * Copies the text in all selected cells in semicolon-separated format to the clipboard.
    */
  def copyTableSelectionAsLineToClipboard(): Unit = {
    val formatted = tableSelectionTextMatrix.flatten mkString "; "
    val clipboardContent = new ClipboardContent
    clipboardContent putString formatted
    Clipboard.getSystemClipboard setContent clipboardContent
  }


  /**
    * Reads text contents out of all selected cells.
    */
  def tableSelectionTextMatrix: Seq[Seq[String]] = {

    def linearColumns(columns: Seq[TableColumn[_, _]]) =
      columns flatMap childColumns

    def childColumns(column: TableColumn[_, _]): Seq[TableColumn[_, _]] =
      column.getColumns match {
        case xs if xs.isEmpty => Seq(column)
        case xs => (xs flatMap childColumns).toSeq
      }

    val cells = getSelectionModel.getSelectedCells
    val rows = cells groupBy (_.getRow)
    val matrix = rows.toSeq sortBy (_._1) map (_._2 sortBy (_.getColumn))
    matrix map { row =>
      row.toSeq map { tp =>
        val columns = getColumns
        val dataColumns = linearColumns(columns.toSeq)
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
                case _ => ""
              }
            }
            else text
          case _ =>
            data.toString
        }
      }
    }
  }

}