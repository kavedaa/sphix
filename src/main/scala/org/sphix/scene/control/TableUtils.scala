package org.sphix.scene.control

import javafx.beans.value.ObservableValue
import javafx.util.Callback
import org.sphix.util._
import javafx.scene.control.TableView
import javafx.scene.control.TableColumn
import javafx.scene.control.TableCell

trait TableUtils[S] extends TableCells[S] { this: TableView[S] =>

  class Column[T](prefWidth: Optional[Double])(text: String, f: S => ObservableValue[T])
    extends TableColumn[S, T](text) with ColumnCells[S, T] {

    def this(text: String, f: S => ObservableValue[T]) =
      this(Absent)(text, f)

    prefWidth ifPresent setPrefWidth

    setCellValueFactory(new Callback[TableColumn.CellDataFeatures[S, T], ObservableValue[T]] {
      def call(cdf: TableColumn.CellDataFeatures[S, T]) = f(cdf.getValue)
    })

    /**
     * Utility method for setting a cell factory without bothering about
     * specifying it as a function of a TableColumn parameter.
     */
    def setCell(tableCell: => TableCell[S, T]) {
      setCellFactory(new Callback[TableColumn[S, T], TableCell[S, T]] {
        def call(c: TableColumn[S, T]) = tableCell
      })
    }
  }
}
