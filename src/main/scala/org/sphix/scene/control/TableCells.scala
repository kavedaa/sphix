package org.sphix.scene.control

import javafx.scene.control.TableColumn
import javafx.scene.control.TableCell
import javafx.scene.image.Image
import javafx.scene.Node
import org.sphix.util.RightConverter
import javafx.beans.property.Property
import java.text._
import java.time._
import java.time.format.DateTimeFormatter
import org.sphix.util.DefaultConverter
import javafx.geometry.Pos
import org.sphix.collection.ObservableSeq

trait TableCells[S] {

  trait TextCell[T] extends TableCell[S, T] with cell.TextCell[T]
  trait GraphicCell[T] extends TableCell[S, T] with cell.GraphicCell[T]
  trait AlignedCell[T] extends TableCell[S, T]
  trait ImageCell[T] extends TableCell[S, T] with cell.ImageCell[T]
  trait BooleanTextCell extends TableCell[S, Boolean] with cell.BooleanTextCell
  trait BooleanOptionTextCell extends TableCell[S, Option[Boolean]] with cell.BooleanOptionTextCell
  trait BooleanImageCell extends TableCell[S, Boolean] with cell.BooleanImageCell
  trait EditableBooleanImageCell extends TableCell[S, Boolean] with cell.EditableBooleanImageCell
  trait TooltipCell[T] extends TableCell[S, T] with cell.TooltipCell[T]
  trait BigDecimalCell extends TableCell[S, BigDecimal] with cell.BigDecimalCell
  trait BigDecimalOptionCell extends TableCell[S, Option[BigDecimal]] with cell.BigDecimalOptionCell
  trait DateCell extends TableCell[S, java.util.Date] with cell.DateCell
  trait DateOptionCell extends TableCell[S, Option[java.util.Date]] with cell.DateOptionCell
  trait LocalDateCell extends TableCell[S, LocalDate] with cell.LocalDateCell
  trait LocalDateOptionCell extends TableCell[S, Option[LocalDate]] with cell.LocalDateOptionCell
  trait LocalDateTimeCell extends TableCell[S, LocalDateTime] with cell.LocalDateTimeCell
  trait LocalDateTimeOptionCell extends TableCell[S, Option[LocalDateTime]] with cell.LocalDateTimeOptionCell
  trait CSSCell[T] extends TableCell[S, T] with cell.CSSTableCell[S, T]
  trait TextFieldCell[T] extends cell.TextFieldTableCell[S, T]
  trait CheckBoxCell[T] extends cell.CheckBoxTableCell[S, T]
  trait DatePickerCell extends cell.DatePickerTableCell[S]
}

trait ColumnCells[S, T] {

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

  trait TextGraphicCell extends TextCell with GraphicCell

  object TextGraphicCell {
    def apply(text0: T => String, graphic0: T => Option[Node]) = new TextGraphicCell {
      def text(item: T) = text0(item)
      def graphic(item: T) = graphic0(item)
    }
  }

  trait StyleCell extends cell.StyleCell[T]

  object StyleCell {
    def apply(style0: T => String) = new StyleCell {
      def style(item: T) = style0(item)
    }
  }

  trait AlignedCell extends TableCell[S, T] with cell.AlignedCell[T]

  object AlignedCell {
    def apply(pos0: T => Pos) = new TextCell with AlignedCell {
      def text(item: T) = item.toString
      def pos(item: T) = pos0(item)
    }
  }

  trait ImageCell extends TableCell[S, T] with cell.ImageCell[T]

  object ImageCell {
    def apply(image0: T => Option[Image]) = new ImageCell {
      def image(item: T) = image0(item)
    }
  }

  trait BooleanTextCell extends TableCell[S, Boolean] with cell.BooleanTextCell

  object BooleanTextCell {
    def apply(trueText0: String, falseText0: String = "") = new BooleanTextCell {
      def trueText = trueText0
      def falseText = falseText0
    }
  }

  trait BooleanOptionTextCell extends TableCell[S, Option[Boolean]] with cell.BooleanOptionTextCell

  object BooleanOptionTextCell {
    def apply(trueText0: String, falseText0: String = "") = new BooleanOptionTextCell {
      def trueText = trueText0
      def falseText = falseText0
    }
  }

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
    def apply(text0: T => String, tooltip0: T => Option[String]) = new TooltipCell {
      def text(item: T) = text0(item)
      def tooltipText(item: T) = tooltip0(item)
    }
  }

  trait BigDecimalCell extends TableCell[S, BigDecimal] with cell.BigDecimalCell
  trait BigDecimalOptionCell extends TableCell[S, Option[BigDecimal]] with cell.BigDecimalOptionCell

  object BigDecimalCell {
    def apply(dcf0: DecimalFormat) = new BigDecimalCell {
      def dcf = dcf0
    }
  }

  object BigDecimalOptionCell {
    def apply(dcf0: DecimalFormat) = new BigDecimalOptionCell {
      def dcf = dcf0
    }
  }

  trait DateCell extends TableCell[S, java.util.Date] with cell.DateCell
  trait DateOptionCell extends TableCell[S, Option[java.util.Date]] with cell.DateOptionCell

  object DateCell {
    def apply(df0: DateFormat = DateFormat.getInstance) = new DateCell {
      def df = df0
    }
  }

  object DateOptionCell {
    def apply(df0: DateFormat = DateFormat.getInstance) = new DateOptionCell {
      def df = df0
    }
  }

  trait LocalDateCell extends TableCell[S, LocalDate] with cell.LocalDateCell
  trait LocalDateOptionCell extends TableCell[S, Option[LocalDate]] with cell.LocalDateOptionCell

  object LocalDateCell {
    def apply(formatter0: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE) =
      new LocalDateCell {
        def formatter = formatter0
      }
  }

  object LocalDateOptionCell {
    def apply(formatter0: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE) =
      new LocalDateOptionCell {
        def formatter = formatter0
      }
  }

  trait LocalDateTimeCell extends TableCell[S, LocalDateTime] with cell.LocalDateTimeCell
  trait LocalDateTimeOptionCell extends TableCell[S, Option[LocalDateTime]] with cell.LocalDateTimeOptionCell

  object LocalDateTimeCell {
    def apply(formatter0: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME) =
      new LocalDateTimeCell {
        def formatter = formatter0
      }
  }

  object LocalDateTimeOptionCell {
    def apply(formatter0: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME) =
      new LocalDateTimeOptionCell {
        def formatter = formatter0
      }
  }

  trait HyperlinkCell extends TableCell[S, T] with cell.HyperlinkCell[T]

  object HyperlinkCell {
    def apply(text0: T => String, graphic0: T => Option[Node], action0: T => Unit) =
      new HyperlinkCell {
        def text(item: T) = text0(item)
        def graphic(item: T) = graphic0(item)
        def action(item: T) = action0(item)
      }
  }

  trait CSSCell extends TableCell[S, T] with cell.CSSTableCell[S, T]

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

  trait ComboBoxCell extends cell.ComboBoxTableCell[S, T]

  object ComboBoxCell {
    def apply(items0: S => ObservableSeq[T], f0: T => String) = new ComboBoxCell {
      def items(s: S) = items0(s)
      def f(t: T) = f0(t)
    }
  }

  trait DatePickerCell extends cell.DatePickerTableCell[S]

  object DatePickerCell {
    def apply(formatter0: DateTimeFormatter) = new DatePickerCell {
      def formatter = formatter0
    }
  }
}