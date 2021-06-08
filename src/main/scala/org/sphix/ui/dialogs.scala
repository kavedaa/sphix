package org.sphix.ui

import scala.util._

import javafx.scene.Node
import javafx.scene.control._

import org.sphix.Val
import org.sphix.scene.control._
import org.sphix.collection.ObservableSeq
import org.sphix.collection.ObservableSeq._
import javafx.scene.image.Image

class ErrorDialog(header: String, message: String)
  extends Dialog[Nothing] {

  val textArea = new TextArea {
    setEditable(false)
    setText(message)
    setWrapText(true)
  }

  setTitle("Error")
  getDialogPane.setHeaderText(header)
  getDialogPane.setContent(textArea)

  //  a little trick to get the default graphic used in alerts
  val img = new Label
  img.getStyleClass.addAll("alert", "error", "dialog-pane")
  setGraphic(img)

  getDialogPane.getButtonTypes add ButtonType.CLOSE
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
}

class ContentDialog(title: String, content: Node)
  extends Dialog[Nothing] {

  setTitle(title)
  getDialogPane.setContent(content)

  getDialogPane.getButtonTypes add ButtonType.CLOSE

  setResizable(true)
}
