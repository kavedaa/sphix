package org.sphix.scene.control

import javafx.scene.control.TableColumn
import javafx.scene.control.TableCell
import javafx.scene.image.Image
import javafx.scene.Node
import org.sphix.util.RightConverter
import javafx.beans.property.Property
import java.text.DateFormat

trait TableCells[S, T] { 

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

  trait AlignmentCell extends TableCell[S, T] with cell.AlignmentCell[T]
  
  trait BooleanImageCell extends TableCell[S, Boolean] with cell.BooleanImageCell

  object BooleanImageCell {
    def apply(trueImage0: Option[Image], falseImage0: Option[Image] = None) = new BooleanImageCell {
      def trueImage = trueImage0
      def falseImage = falseImage0
    }
  }

  trait EditableBooleanImageCell extends TableCell[S, Boolean] with cell.EditableBooleanImageCell

  object EditableBooleanImageCell {
    def apply(trueImage0: Option[Image], falseImage0: Option[Image] = None) = new EditableBooleanImageCell {
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

  trait DateCell extends TableCell[S, java.util.Date] with cell.DateCell 
  
  object DateCell {
    def apply(df0: DateFormat = DateFormat.getInstance) = new DateCell {
      def df = df0
    }
  }
  
  trait DateOptionCell extends TableCell[S, Option[java.util.Date]] with cell.DateOptionCell 
  
  object DateOptionCell {
    def apply(df0: DateFormat = DateFormat.getInstance) = new DateOptionCell {
      def df = df0
    }
  }
  
  trait TextFieldCell extends cell.TextFieldTableCell[S, T]

  object TextFieldCell {
    def apply(converter0: RightConverter[T, String]) = new TextFieldCell {
      def converter = converter0
    }
  }
  
  trait CheckBoxCell extends cell.CheckBoxTableCell[S, T]
  
  object CheckBoxCell {
    def apply(f0: S => Property[Boolean]) = new CheckBoxCell {
      def f(s: S) = f0(s)
    }
  }
}