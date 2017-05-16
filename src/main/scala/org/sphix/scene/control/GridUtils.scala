package org.sphix.scene.control

import javafx.util.Callback

import org.controlsfx.control.{GridCell, GridView}

trait GridUtils[T] extends GridView[T] with GridCells[T] {

  def setCell(gridCell: => GridCell[T]) {
    setCellFactory(new Callback[GridView[T], GridCell[T]] {
      def call(c: GridView[T]) = gridCell
    })
  }

}
