package org.sphix.dialog

import org.sphix.util.RightConverter
import javafx.scene.control._
import javafx.scene.layout._
import org.sphix.Var._
import javafx.event.ActionEvent
import org.sphix.collection.ObservableSeq
import javafx.scene.Node
import javafx.util.Callback
import org.sphix.util._
import org.sphix.collection.mutable.ObservableBuffer
import javafx.scene.control.Dialog
import javafx.scene.control.ButtonBar.ButtonData
import org.sphix.Var._
import org.sphix.util._

class InputDialog[A, N <: Node](val inputControl: N, title: String, inputTitle: String, contentDisplay: ContentDisplay)
  extends Dialog[A] { dialog =>

  val content =
    new GridPane {
      setHgap(10)
      setVgap(10)
      val label = new Label(inputTitle)
      getChildren addAll (label, inputControl)
      GridPane setHgrow (inputControl, Priority.ALWAYS)
      contentDisplay match {
        case ContentDisplay.RIGHT =>
          GridPane.setColumnIndex(inputControl, 1);
        case _ =>
          GridPane.setRowIndex(inputControl, 1);
      }
    }

  setTitle(title)
  setResizable(false)
  getDialogPane setContent content

  val okButtonType = new ButtonType("OK", ButtonData.OK_DONE)

  getDialogPane.getButtonTypes addAll (ButtonType.CANCEL, okButtonType)

  val okButton = (getDialogPane lookupButton okButtonType).asInstanceOf[Button]
}

class TextFieldDialog[T](converter: RightConverter[T, String], title: String, inputTitle: String)
  extends InputDialog[Option[T], TextField](new TextField, title, inputTitle, ContentDisplay.RIGHT) {

  val value = inputControl.textProperty map converter.deconvert

  val valueDefined = value map (_.isEmpty)
  okButton.disableProperty <== valueDefined.as

  setResultConverter { (dialogButton: ButtonType) =>
    if (dialogButton == okButtonType) value()
    else null
  }

  def input(initValue: Option[T] = None) = {
    initValue foreach (v => inputControl setText (converter convert v))
    inputControl requestFocus ()
    val res = showAndWait()
    if (res.isPresent) res.get else None
  }
}

class TextAreaDialog(title: String, inputTitle: String)
  extends InputDialog[String, TextArea](new TextArea, title, inputTitle, ContentDisplay.BOTTOM) {

  setResultConverter { (dialogButton: ButtonType) =>
    if (dialogButton == okButtonType) inputControl.getText
    else null
  }

  def input(initValue: Option[String] = None) = {
    initValue foreach (v => inputControl setText v)
    inputControl requestFocus ()
    val res = showAndWait()
    if (res.isPresent) Some(res.get) else None
  }
}

class ComboBoxDialog[T](cellFactory: => ListCell[T], title: String, inputTitle: String)
  extends InputDialog[Option[T], ComboBox[T]](new ComboBox[T], title, inputTitle, ContentDisplay.RIGHT) {

  inputControl setMinWidth 150
  inputControl setCellFactory ((_: ListView[T]) => cellFactory)
  inputControl setButtonCell (cellFactory)

  okButton.disableProperty <== inputControl.getSelectionModel.selectedItemProperty.isNull

  def f(i: Int) = if (i == 0) inputControl.getSelectionModel.getSelectedItem else null

  setResultConverter { (dialogButton: ButtonType) =>
    if (dialogButton == okButtonType) Some(inputControl.getSelectionModel.getSelectedItem)
    else null
  }

  def input(items: Iterable[T], initValue: Option[T] = None) = {
    inputControl setItems items.to[ObservableSeq]
    initValue foreach (v => inputControl.getSelectionModel select v)
    inputControl requestFocus ()
    val res = showAndWait()
    if (res.isPresent) res.get else None
  }
}

class CheckBoxesDialog[T](title: String, inputTitle: String, values: Seq[T], selected: Seq[T], render: T => String)
  extends InputDialog[Seq[T], VBox](new VBox(10), title, inputTitle, ContentDisplay.BOTTOM) {

  val cbs = values map { v =>
    v -> new CheckBox(render(v)) {
      setSelected(selected contains v)
    }
  }

  inputControl.getChildren setAll (cbs.to[ObservableSeq] map (_._2))

  setResultConverter { (dialogButton: ButtonType) =>
    if (dialogButton == okButtonType) cbs filter (_._2.isSelected) map (_._1)
    else null
  }

  def input(): Option[Seq[T]] = {
    inputControl requestFocus ()
    val res = showAndWait()
    if (res.isPresent) Some(res.get) else None
  }
}

class PasswordDialog(title: String, inputTitle: String, authenticator: String => Boolean)
  extends InputDialog[Boolean, PasswordField](new PasswordField, title, inputTitle, ContentDisplay.BOTTOM) {

  val correct = inputControl.textProperty delay 250 map authenticator

  okButton.disableProperty <== correct map (x => !x)

  setResultConverter { (dialogButton: ButtonType) =>
    if (dialogButton == okButtonType) correct()
    else false
  }

  def input() = {
    inputControl requestFocus ()
    val res = showAndWait()
    if (res.isPresent) res.get else false
  }
}