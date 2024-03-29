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

import scala.util._

trait Cell[T] extends javafx.scene.control.Cell[T] {

  type Item = T

  def onUpdate(item: T): Unit = {}

  override def updateItem(item: T, empty: Boolean) = {
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

trait StripNewLines[T] extends TextCell[T] {

  override def onUpdate(item: T) = {
    super.onUpdate(item)
    setText(text(item) split "\r\n" mkString " ")
  }
}

trait GraphicCell[T] extends Cell[T] {

  def graphic(item: T): Option[Node]

  override def onUpdate(item: T) = {
    super.onUpdate(item)
    setGraphic(graphic(item).orNull)
  }
}

trait StringOptionCell extends TextCell[Option[String]] {
  def text(item: Option[String]) = item match {
    case Some(s) => s
    case None => ""
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

  def pos(item: T): Pos

  override def onUpdate(item: T) = {
    super.onUpdate(item)
    setAlignment(pos(item))
  }
}

trait ImageCell[T] extends AlignedCell[T] {

  def image(t: T): Option[Image]

  private lazy val imageView = new ImageView

  def pos(item: T) = Pos.CENTER

  override def onUpdate(item: T) = {
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

trait BooleanTextCell extends TextCell[Boolean] {

  def trueText: String
  def falseText: String

  def text(item: Boolean) = if (item) trueText else falseText
}

trait BooleanOptionTextCell extends TextCell[Option[Boolean]] {

  def trueText: String
  def falseText: String

  def text(item: Option[Boolean]) =
    if (item contains true) trueText else if (item contains false) falseText else ""
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

//  def text(item: T): String
  def tooltipText(item: T): Option[String]

  lazy val tooltip = new Tooltip
//  lazy val label = new Label

  override def onUpdate(item: T) = {
    super.onUpdate(item)
//    label setText (text(item) replaceAll ("[\n\r]", " "))
//    setGraphic(label)
    tooltipText(item) map { t =>
      tooltip setText t
      setTooltip(tooltip)
    } getOrElse setTooltip(null)

  }
}

trait BigDecimalCell extends TextCell[BigDecimal] with AlignedCell[BigDecimal] {
  def dcf: DecimalFormat
  def text(x: BigDecimal) = dcf format x
  def pos(item: BigDecimal) = Pos.CENTER_RIGHT
}

trait BigDecimalOptionCell extends TextCell[Option[BigDecimal]] with AlignedCell[Option[BigDecimal]] {
  def dcf: DecimalFormat
  def text(x: Option[BigDecimal]) = x map dcf.format getOrElse ""
  def pos(item: Option[BigDecimal]) = Pos.CENTER_RIGHT
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
  def text(ld: LocalDate) = formatter format ld
}

trait LocalDateOptionCell extends TextCell[Option[LocalDate]] {
  def formatter: DateTimeFormatter
  def text(ld: Option[LocalDate]) = ld map formatter.format getOrElse ""
}

trait LocalDateTryCell extends TextCell[Try[LocalDate]] {
  def formatter: DateTimeFormatter
  def text(ld: Try[LocalDate]) = ld match {
    case Success(x) => success(x)
    case Failure(ex) => failure(ex)
  }
  def success(x: LocalDate) = formatter format x
  def failure(ex: Throwable) = ex.getMessage
}

trait LocalDateTimeCell extends TextCell[LocalDateTime] {
  def formatter: DateTimeFormatter
  def text(ldt: LocalDateTime) = formatter format ldt
}

trait LocalDateTimeTryCell extends TextCell[Try[LocalDateTime]] {
  def formatter: DateTimeFormatter
  def text(ldt: Try[LocalDateTime]) = ldt match {
    case Success(x) => success(x)
    case Failure(ex) => failure(ex)
  }
  def success(x: LocalDateTime) = formatter format x
  def failure(ex: Throwable) = ex.getMessage
}

trait LocalDateTimeOptionCell extends TextCell[Option[LocalDateTime]] {
  def formatter: DateTimeFormatter
  def text(ldt: Option[LocalDateTime]) = ldt map formatter.format getOrElse ""
}

trait HyperlinkCell[T] extends Cell[T] {

  def isHyperlink(item: T) = true
  def action(item: T): Unit

  lazy val hyperlink = new Hyperlink

  //  TODO there's a bug when combining this with ImageCell
  //  in some scenarios the image is not displayed

  override def onUpdate(item: T) = {
    super.onUpdate(item)
    if (isHyperlink(item)) {
      hyperlink setText getText
      if (getGraphic ne hyperlink) hyperlink setGraphic getGraphic
      hyperlink setOnAction { () => action(item) }
      setGraphic(hyperlink)
      setText(null)
    }
    else {
      if (getGraphic eq hyperlink) setGraphic(null)
    }
  }
}

trait ProxyCell[T, U] extends Cell[T] {

  def underlying: Cell[U]

  def f: T => U

  override def onUpdate(item: T) = underlying onUpdate f(item)

}