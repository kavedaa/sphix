package org.sphix.scene.control.cell

import javafx.scene.control.Cell
import javafx.scene.control.ListCell
import javafx.scene.control.TableCell
import javafx.util.Callback
import javafx.scene.control.ListView
import javafx.scene.control.TableColumn
import javafx.scene.Node
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import java.text.NumberFormat
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.Tooltip

trait TextCell[T] extends Cell[T] {

  def text(item: T): String

  override def updateItem(item: T, empty: Boolean) {
    super.updateItem(item, empty)
    if (!empty) {
      setText(text(item))
    }
    else {
      setText(null)
    }
  }
}

trait GraphicCell[T] extends Cell[T] {

  def graphic(item: T): Option[Node]

  override def updateItem(item: T, empty: Boolean) {
    super.updateItem(item, empty)
    if (!empty) {
      setGraphic(graphic(item).orNull)
    }
    else {
      setGraphic(null)
    }
  }
}

trait ImageCell[T] extends Cell[T] {

  def image(t: T): Option[Image]

  private lazy val imageView = new ImageView

  override def updateItem(item: T, empty: Boolean) {
    super.updateItem(item, empty)
    if (!empty) {
      image(item) match {
        case Some(img) =>
          imageView setImage img
          setGraphic(imageView)
        case None =>
          setGraphic(null)
      }
    }
    else {
      setGraphic(null)
    }
  }
}

trait BooleanImageCell extends ImageCell[Boolean] {

  def trueImage: Option[Image]
  def falseImage: Option[Image]

  setAlignment(Pos.CENTER)

  def image(item: Boolean) = if (item) trueImage else falseImage
}

trait TooltipCell[T] extends Cell[T] {

  def text(item: T): String
  def tooltip(item: T): String

  lazy val tooltip = new Tooltip
  lazy val label = new Label { setTooltip(tooltip) }

  override def updateItem(item: T, empty: Boolean) {
    super.updateItem(item, empty)
    if (!empty) {
      label setText text(item)
      tooltip setText tooltip(item)
      setGraphic(label)
    }
    else {
      setGraphic(null)
    }
  }
}

trait AlignedCell[T] extends TextCell[T] {
  def position: Pos
  setAlignment(position)
}

trait NumericCell[T] extends AlignedCell[T] {
  def nf: NumberFormat
  def position = Pos.CENTER_RIGHT
  def text(t: T) = nf format t
}
//
//class StaticImageTableCell[S, T](val imageFileName: String, val show: T => Boolean)(implicit val cls: Class[_]) extends TableCell[S, T] with StaticImageCell[T]


//	Factories

//object TextTableCell {
//  def apply[S, T](text: T => String) = new Callback[TableColumn[S, T], TableCell[S, T]] {
//    def call(c: TableColumn[S, T]) = new TextTableCell(text)
//  }
//}
//
//object AlignedListCell {
//  def apply[T](pos: Pos) = new Callback[ListView[T], ListCell[T]] {
//    def call(v: ListView[T]) = new AlignedListCell(pos)
//  }
//}
//
//object AlignedTableCell {
//  def apply[S, T](pos: Pos) = new Callback[TableColumn[S, T], TableCell[S, T]] {
//    def call(c: TableColumn[S, T]) = new AlignedTableCell(pos)
//  }
//}
//
//object NumericListCell {
//  def apply[T](nf: NumberFormat) = new Callback[ListView[T], ListCell[T]] {
//    def call(v: ListView[T]) = new NumericListCell(nf)
//  }
//}
//
//object NumericTableCell {
//  def apply[S, T](nf: NumberFormat) = new Callback[TableColumn[S, T], TableCell[S, T]] {
//    def call(v: TableColumn[S, T]) = new NumericTableCell(nf)
//  }
//}
//
//object NodeListCell {
//  def apply[T](node: T => Option[Node]) = new Callback[ListView[T], ListCell[T]] {
//    def call(v: ListView[T]) = new NodeListCell(node)
//  }
//}
//
//object NodeTableCell {
//  def apply[S, T](node: T => Option[Node]) = new Callback[TableColumn[S, T], TableCell[S, T]] {
//    def call(c: TableColumn[S, T]) = new NodeTableCell(node)
//  }
//}
//
//object NodeTextListCell {
//  def apply[T](node: T => Option[Node], text: T => String) = new Callback[ListView[T], ListCell[T]] {
//    def call(v: ListView[T]) = new NodeTextListCell(node, text)
//  }
//}
//
//object NodeTextTableCell {
//  def apply[S, T](node: T => Option[Node], text: T => String) = new Callback[TableColumn[S, T], TableCell[S, T]] {
//    def call(c: TableColumn[S, T]) = new NodeTextTableCell(node, text)
//  }
//}
//
//object ImageTextListCell {
//  def apply[T](image: T => Option[Image], text: T => String) = new Callback[ListView[T], ListCell[T]] {
//    def call(v: ListView[T]) = new ImageTextListCell(image, text)
//  }
//}
//
//object ImageTextTableCell {
//  def apply[S, T](image: T => Option[Image], text: T => String) = new Callback[TableColumn[S, T], TableCell[S, T]] {
//    def call(c: TableColumn[S, T]) = new ImageTextTableCell(image, text)
//  }
//}

//object StaticImageTableCell {
//  def apply[S, T](imageFileName: String, show: T => Boolean)(implicit cls: Class[_]) = new Callback[TableColumn[S, T], TableCell[S, T]] {
//    def call(c: TableColumn[S, T]) = new StaticImageTableCell(imageFileName, show)
//  }
//}
//
