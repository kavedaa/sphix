package org.sphix.scene.control

import javafx.scene.{ control => jfxsc }
import javafx.beans.value.ObservableValue
import javafx.util.Callback
import org.sphix.util._

trait Columns[S] { this: jfxsc.TableView[S] =>

  class Column[T](prefWidth: Optional[Double])(text: String, f: S => ObservableValue[T])
    extends jfxsc.TableColumn[S, T](text) with TableCells[S, T] {

    def this(text: String, f: S => ObservableValue[T]) =
      this(Absent)(text, f)

    prefWidth ifPresent setPrefWidth

    setCellValueFactory(new Callback[jfxsc.TableColumn.CellDataFeatures[S, T], ObservableValue[T]] {
      def call(cdf: jfxsc.TableColumn.CellDataFeatures[S, T]) = f(cdf.getValue)
    })

    /**
     * Utility method for setting a cell factory without bothering about
     * specifying it as a function of a TableColumn parameter.
     */
    def setCell(tableCell: => jfxsc.TableCell[S, T]) {
      setCellFactory(new Callback[jfxsc.TableColumn[S, T], jfxsc.TableCell[S, T]] {
        def call(c: jfxsc.TableColumn[S, T]) = tableCell
      })
    }
  }
}

class TableView[S] extends jfxsc.TableView[S] with Columns[S]