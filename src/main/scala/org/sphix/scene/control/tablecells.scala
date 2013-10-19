package org.sphix.scene.control

import javafx.scene.control.TableColumn
import javafx.scene.control.TableCell
import javafx.scene.image.Image
import javafx.scene.Node

trait TableCells[S, T] { this: TableColumn[S, T] =>

  trait TextCell extends TableCell[S, T] with cell.TextCell[T]

  object TextCell {
    def apply(text0: T => String) = new TextCell {
      def text(item: T) = text0(item)
    }
  }

  trait GraphicCell extends TableCell[S, T] with cell.GraphicCell[T]

  object GraphicCell {
    def apply(graphic0: T => Option[Node]) = new GraphicCell {
      def graphic(item: T) = graphic0(item)
    }
  }

  trait ImageCell extends TableCell[S, T] with cell.ImageCell[T]
  
  object ImageCell {
    def apply(image0: T => Option[Image]) = new ImageCell {
      def image(item: T) = image0(item)
    }
  }

  trait BooleanImageCell extends TableCell[S, Boolean] with cell.BooleanImageCell
  
  object BooleanImageCell {
    def apply(trueImage0: Option[Image], falseImage0: Option[Image]) = new BooleanImageCell {
      def trueImage = trueImage0
      def falseImage = falseImage0
    }
  }
  
  trait TooltipCell extends TableCell[S, T] with cell.TooltipCell[T]
  
  object TooltipCell {
    def apply(text0: T => String, tooltip0: T => String) = new TooltipCell {
      def text(item: T) = text0(item)
      def tooltip(item: T) = tooltip0(item)
    }
  }
}