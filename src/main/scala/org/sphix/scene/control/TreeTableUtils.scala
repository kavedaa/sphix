package org.sphix.scene.control

import javafx.beans.value.ObservableValue
import javafx.util.Callback
import org.sphix.util._
import javafx.scene.control._

trait TreeTableUtils[S] { this: TreeTableView[S] =>

  class Column[T](prefWidth: Optional[Double])(text: String, f: S => ObservableValue[T])
    extends TreeTableColumn[S, T](text) {

    def this(text: String, f: S => ObservableValue[T]) =
      this(Absent)(text, f)

    prefWidth ifPresent setPrefWidth

    setCellValueFactory(new Callback[TreeTableColumn.CellDataFeatures[S, T], ObservableValue[T]] {
      def call(cdf: TreeTableColumn.CellDataFeatures[S, T]) = f(cdf.getValue.getValue)
    })

    /**
     * Utility method for setting a cell factory without bothering about
     * specifying it as a function of a TreeTableColumn parameter.
     */
    def setCell(tableCell: => TreeTableCell[S, T]) = {
      setCellFactory(new Callback[TreeTableColumn[S, T], TreeTableCell[S, T]] {
        def call(c: TreeTableColumn[S, T]) = tableCell
      })
    }
  }

  class HeaderColumn(prefWidth: Optional[Double])(text: String, subColumns: TreeTableColumn[S, _]*)
    extends TreeTableColumn[S, Nothing](text) {
    
    def this(text: String, subColumns: TreeTableColumn[S, _]*) =
      this(Absent)(text, subColumns: _*)
    
    prefWidth ifPresent setPrefWidth
    getColumns.addAll(subColumns: _*)
  }
}
