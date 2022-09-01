package org.sphix.ui

import scala.util._

import javafx.scene.Node
import javafx.scene.control._
import javafx.scene.layout._

import org.sphix.Val
import org.sphix.scene.control._
import org.sphix.collection.ObservableSeq
import org.sphix.collection.ObservableSeq._
import javafx.scene.image.Image

class InfoDialog(title: Option[String], header: Option[String], message: String)
  extends Dialog[Nothing] {

  def this(title: String, header: String, message: String) = this(Some(title), Some(header), message)
  def this(header: String, message: String) = this(None, Some(header), message)
  def this(title: Option[String], message: String) = this(title, None, message)
  def this(message: String) = this(None, None, message)

  val textArea = new TextArea {
    setEditable(false)
    setWrapText(true)
  }

  title foreach setTitle
  header foreach getDialogPane.setHeaderText
  textArea.setText(message)
  
  getDialogPane.setContent(textArea)

  //  a little trick to get the default graphic used in alerts
  val img = new Label
  img.getStyleClass.addAll("alert", "info", "dialog-pane")
  setGraphic(img)

  getDialogPane.getButtonTypes add ButtonType.CLOSE

  setResizable(true)
}

class ExceptionDialog(title: Option[String], header: Option[String], exception: Throwable)
  extends Dialog[Nothing] {

  def this(title: String, header: String, exception: Throwable) = this(Some(title), Some(header), exception)
  def this(header: String, exception: Throwable) = this(None, Some(header), exception)
  def this(title: Option[String], exception: Throwable) = this(title, None, exception)
  def this(exception: Throwable) = this(None, None, exception)

  val textArea, stackTraceTextArea = new TextArea {
    setEditable(false)
    setWrapText(true)
  }

  title foreach setTitle
  header foreach getDialogPane.setHeaderText
  textArea.setText(exception.getMessage)
  stackTraceTextArea.setText(exception.getStackTrace.mkString("\n"))

  val stackTracePane = new TitledPane {
    setContent(stackTraceTextArea)
    setCollapsible(true)
    setExpanded(false)
  }

  val content = new VBox(textArea, stackTraceTextArea)

  getDialogPane.setContent(content)

  //  a little trick to get the default graphic used in alerts
  val img = new Label
  img.getStyleClass.addAll("alert", "error", "dialog-pane")
  setGraphic(img)

  getDialogPane.getButtonTypes add ButtonType.CLOSE

  setResizable(true)
}

class ErrorDialog(title: Option[String], header: Option[String], message: String)
  extends Dialog[Nothing] {

  def this(title: String, message: String) = this(Some(title), None, message)
  def this(title: Option[String], message: String) = this(title, None, message)
  def this(message: String) = this(None, None, message)

  val textArea= new TextArea {
    setEditable(false)
    setWrapText(true)
  }

  title foreach setTitle
  header foreach getDialogPane.setHeaderText
  textArea.setText(message)
  
  getDialogPane.setContent(textArea)

  //  a little trick to get the default graphic used in alerts
  val img = new Label
  img.getStyleClass.addAll("alert", "error", "dialog-pane")
  setGraphic(img)

  getDialogPane.getButtonTypes add ButtonType.CLOSE

  setResizable(true)
}

class ErrorsDialog[A](title: String, errors: Seq[(A, Failure[_])])(render: A => String)
  extends Dialog[Nothing] {

  val table = new TableView[(A, Failure[_])] with TableUtils[(A, Failure[_])] {

    val item = new Column("Item", x => Val(render(x._1)))
    val error = new Column("Error", x => Val(x._2.exception.getMessage))

    getColumns.addAll(item, error)

    setItems(errors)
  }

  setTitle(title)
  getDialogPane.setHeaderText(title)
  getDialogPane.setContent(table)

  getDialogPane.getButtonTypes add ButtonType.CLOSE

  getDialogPane.setPrefWidth(800)

  setResizable(true)
}

class TrysDialog[A](title: String, xs: Seq[Try[A]])(render: A => String)
  extends Dialog[Nothing] {

  val list = new ListView[Try[A]] with ListUtils[Try[A]] {

    setCell {
      new TextCell {
        def text(item: Try[A]) = item match {
          case Success(value) => render(value)
          case Failure(ex) => ex.getMessage
        }
      }
    }

    setItems(xs)
  }

  setTitle(title)
  getDialogPane.setHeaderText(title)
  getDialogPane.setContent(list)

  getDialogPane.getButtonTypes add ButtonType.CLOSE

  getDialogPane.setPrefWidth(800)

  setResizable(true)
}

class ContentDialog(title: String, content: Node)
  extends Dialog[Nothing] {

  setTitle(title)
  getDialogPane.setContent(content)

  getDialogPane.getButtonTypes add ButtonType.CLOSE

  setResizable(true)
}
