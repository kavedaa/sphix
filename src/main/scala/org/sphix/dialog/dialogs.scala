package org.sphix.dialog

import org.sphix.util.RightConverter
import javafx.scene.control._
import javafx.scene.layout._
import org.controlsfx.dialog.Dialog
import org.sphix.Var._
import org.controlsfx.control.action.AbstractAction
import org.controlsfx.control.ButtonBar
import javafx.event.ActionEvent
import org.controlsfx.control.ButtonBar.ButtonType
import org.sphix.collection.ObservableSeq
import javafx.scene.Node
import javafx.util.Callback
import org.controlsfx.dialog.AbstractDialogAction
import org.controlsfx.dialog.Dialog.ActionTrait
import org.sphix.util._
import org.sphix.collection.mutable.ObservableBuffer

class InputDialog[N <: Node](val inputControl: N, title: String, inputTitle: String, contentDisplay: ContentDisplay)
  extends Dialog(null, title) { dialog =>

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

  val okAction = new AbstractDialogAction("OK", ActionTrait.DEFAULT) {
    ButtonBar setType (this, ButtonType.OK_DONE)
    def execute(e: ActionEvent) { dialog hide () }
  }

  setContent(content)
  setResizable(false)
  setIconifiable(false)
  getActions addAll (okAction, Dialog.Actions.CANCEL)
}

class TextFieldDialog[T](converter: RightConverter[T, String], title: String, inputTitle: String)
  extends InputDialog(new TextField, title, inputTitle, ContentDisplay.RIGHT) {

  val value = inputControl.textProperty map converter.deconvert

  val valueDefined = value map (_.isEmpty)
  okAction.disabledProperty <== valueDefined.as

  def input(initValue: Option[T] = None) = {
    initValue foreach (v => inputControl setText (converter convert v))
    inputControl requestFocus ()
    if (show() == okAction) value() else None
  }
}

class ComboBoxDialog[T](cellFactory: => ListCell[T], title: String, inputTitle: String)
  extends InputDialog(new ComboBox[T], title, inputTitle, ContentDisplay.RIGHT) {

  inputControl setMinWidth 150
  inputControl setCellFactory ((_: ListView[T]) => cellFactory)
  inputControl setButtonCell (cellFactory)

  okAction.disabledProperty <== inputControl.getSelectionModel.selectedItemProperty.isNull

  def input(items: Iterable[T], initValue: Option[T] = None) = {
    inputControl setItems items.to[ObservableSeq]
    initValue foreach (v => inputControl.getSelectionModel select v)
    inputControl requestFocus ()
    if (show() == okAction) Option(inputControl.getSelectionModel.getSelectedItem) else None
  }
}

class CheckBoxesDialog(title: String, inputTitle: String)
  extends InputDialog(new VBox(10), title, inputTitle, ContentDisplay.BOTTOM) {

  def input[T](values: Seq[T], selected: Seq[T])(render: T => String): Option[Seq[T]] = {
    val cbs = values map { v =>
      v -> new CheckBox(render(v)) {
        setSelected(selected contains v)
      }
    }
    inputControl.getChildren setAll (cbs.to[ObservableSeq] map (_._2))
    inputControl requestFocus ()
    if (show() == okAction) Some(cbs filter (_._2.isSelected) map (_._1))
    else None
  }
}

class PasswordDialog(title: String, inputTitle: String, authenticator: String => Boolean)
  extends InputDialog(new PasswordField, title, inputTitle, ContentDisplay.BOTTOM) {

  val correct = inputControl.textProperty delay 250 map authenticator

  okAction.disabledProperty <== correct map (x => !x)

  def input() = {
    inputControl requestFocus ()
    show() == okAction
  }
}