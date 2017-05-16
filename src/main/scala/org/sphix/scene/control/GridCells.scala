package org.sphix.scene.control

import javafx.scene.Node

import org.controlsfx.control.GridCell

trait GridCells[T] {

  trait TextCell extends GridCell[T] with cell.TextCell[T]

  object TextCell {
    def apply(text0: T => String) = new TextCell {
      def text(item: T) = text0(item)
    }
  }

  trait GraphicCell extends GridCell[T] with cell.GraphicCell[T]

  object GraphicCell {
    def apply(graphic0: T => Option[Node]) = new GraphicCell {
      def graphic(item: T) = graphic0(item)
    }
  }


}
