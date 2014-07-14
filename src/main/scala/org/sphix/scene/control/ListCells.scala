package org.sphix.scene.control

import javafx.scene.control.ListCell
import javafx.scene.image.Image
import javafx.scene.Node

trait ListCells[T] { 

  trait TextCell extends ListCell[T] with cell.TextCell[T]

  object TextCell {
    def apply(text0: T => String) = new TextCell {
      def text(item: T) = text0(item)
    }
  }

  trait GraphicCell extends ListCell[T] with cell.GraphicCell[T]

  object GraphicCell {
    def apply(graphic0: T => Option[Node]) = new GraphicCell {
      def graphic(item: T) = graphic0(item)
    }
  }

  trait ImageCell extends ListCell[T] with cell.ImageCell[T]

  object ImageCell {
    def apply(image0: T => Option[Image]) = new ImageCell {
      def image(item: T) = image0(item)
    }
  }
  
  trait AlignmentCell extends ListCell[T] with cell.AlignmentCell[T]  

  trait BooleanImageCell extends ListCell[Boolean] with cell.BooleanImageCell

  object BooleanImageCell {
    def apply(trueImage0: Option[Image], falseImage0: Option[Image]) = new BooleanImageCell {
      def trueImage = trueImage0
      def falseImage = falseImage0
    }
  }

}