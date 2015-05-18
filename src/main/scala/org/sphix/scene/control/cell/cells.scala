package org.sphix.scene.control.cell

import javafx.scene.control.ListCell
import javafx.scene.control.TableCell
import javafx.scene.control.ListView
import javafx.scene.control.TableColumn
import javafx.scene.Node
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.Tooltip
import org.sphix.util._
import java.text.DateFormat
import java.text.DecimalFormat
import java.time._
import java.time.format.DateTimeFormatter
import javafx.scene.control.Hyperlink

trait Cell[T] extends javafx.scene.control.Cell[T] {

  type Item = T
  
  def onUpdate(item: T): Unit = {}

  override def updateItem(item: T, empty: Boolean) {
    super.updateItem(item, empty)
    if (!empty) onUpdate(item)
    else {
      setText(null)
      setGraphic(null)
    }
  }
}

trait TextCell[T] extends Cell[T] {

  def text(item: T): String

  override def onUpdate(item: T) = {
    super.onUpdate(item)
    setText(text(item))
  }
}

trait GraphicCell[T] extends Cell[T] {

  def graphic(item: T): Option[Node]

  override def onUpdate(item: T) = {
    super.onUpdate(item)
    setGraphic(graphic(item).orNull)
  }
}

trait StyleCell[T] extends Cell[T] {
  
  def style(item: T): String
  
  override def onUpdate(item: T) = {
    super.onUpdate(item)
    setStyle(style(item))
  }  
}

trait AlignedCell[T] extends Cell[T] {

  def pos: Pos

  override def onUpdate(item: T) = {
    super.onUpdate(item)
    setAlignment(pos)
  }
}

trait ImageCell[T] extends AlignedCell[T] {

  def image(t: T): Option[Image]

  private lazy val imageView = new ImageView

  def pos = Pos.CENTER

  override def onUpdate(item: T) {
    super.onUpdate(item)
    image(item) match {
      case Some(img) =>
        imageView setImage img
        setGraphic(imageView)
      case None =>
        setGraphic(null)
    }
  }
}

trait BooleanImageCell extends ImageCell[Boolean] {

  def trueImage: Option[Image]
  def falseImage: Option[Image]

  def image(item: Boolean) = if (item) trueImage else falseImage
}

trait EditableBooleanImageCell extends BooleanImageCell {

  setOnMouseClicked { () =>
    commitEdit(!getItem)
  }
}

trait TooltipCell[T] extends Cell[T] {

  def text(item: T): String
  def tooltip(item: T): String

  lazy val tooltip = new Tooltip
  lazy val label = new Label { setTooltip(tooltip) }

  override def onUpdate(item: T) {
    super.onUpdate(item)
    label setText (text(item) replaceAll ("[\n\r]", " "))
    tooltip setText tooltip(item)
    setGraphic(label)
  }
}

trait BigDecimalCell extends TextCell[BigDecimal] with AlignedCell[BigDecimal] {
  def dcf: DecimalFormat
  def text(x: BigDecimal) = dcf format x
  def pos = Pos.CENTER_RIGHT
}

trait BigDecimalOptionCell extends TextCell[Option[BigDecimal]] with AlignedCell[Option[BigDecimal]] {
  def dcf: DecimalFormat
  def text(x: Option[BigDecimal]) = x map dcf.format getOrElse ""
  def pos = Pos.CENTER_RIGHT
}

trait DateCell extends TextCell[java.util.Date] {
  def df: DateFormat
  def text(date: java.util.Date) = df format date
}

trait DateOptionCell extends TextCell[Option[java.util.Date]] {
  def df: DateFormat
  def text(date: Option[java.util.Date]) = date map df.format getOrElse ""
}

trait LocalDateCell extends TextCell[LocalDate] {
  def formatter: DateTimeFormatter
  def text(ldt: LocalDate) = formatter format ldt
}

trait LocalDateOptionCell extends TextCell[Option[LocalDate]] {
  def formatter: DateTimeFormatter
  def text(ldt: Option[LocalDate]) = ldt map formatter.format getOrElse ""
}

trait LocalDateTimeCell extends TextCell[LocalDateTime] {
  def formatter: DateTimeFormatter
  def text(ldt: LocalDateTime) = formatter format ldt
}

trait LocalDateTimeOptionCell extends TextCell[Option[LocalDateTime]] {
  def formatter: DateTimeFormatter
  def text(ldt: Option[LocalDateTime]) = ldt map formatter.format getOrElse ""
}

trait HyperlinkCell[T] extends Cell[T] {
  
  def text(item: T): String
  def action(item: T): Unit
  
  lazy val hyperlink = new Hyperlink
  
  override def onUpdate(item: T) = {
    super.onUpdate(item)
    hyperlink setText text(item)
    hyperlink setOnAction { () => action(item) }
    setGraphic(hyperlink)
  }
}