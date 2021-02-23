package org.sphix.ui

import javafx.scene.layout._
import javafx.scene.control._
import javafx.scene.input._

import org.controlsfx.control.action._

import org.sphix.collection._

abstract class CrudPane(
  add: => Unit,
  edit: => Unit,
  delete: => Unit)
  (implicit texts: CrudTexts)
  extends BorderPane {

  val addAction = new Action(texts.Add + "...", _ => add)
  val editAction = new Action(texts.Edit + "...", _ => edit)
  val deleteAction = new Action(texts.Delete, _ => delete)

  val toolbar = ActionUtils.createToolBar(ObservableSeq(editAction, deleteAction), ActionUtils.ActionTextBehavior.SHOW)

  toolbar.getItems.add(0, ActionUtils.createButton(addAction))

  def table: TableView[_]

  def init() = {

    addEventHandler(KeyEvent.KEY_PRESSED, { (keyEvent: KeyEvent) =>    
      val addCombination = new KeyCodeCombination(KeyCode.INSERT)
      if (addCombination `match` keyEvent) add
    })
    
    table.addEventHandler(KeyEvent.KEY_PRESSED, { (keyEvent: KeyEvent) =>    
      val editCombination = new KeyCodeCombination(KeyCode.ENTER, KeyCombination.SHIFT_DOWN)
      val deleteCombination = new KeyCodeCombination(KeyCode.DELETE)
      if (editCombination `match` keyEvent) edit
      if (deleteCombination `match` keyEvent) delete
    })

    table.addEventHandler(MouseEvent.MOUSE_CLICKED, { (mouseEvent: MouseEvent) =>
      if (mouseEvent.getClickCount == 2) edit
    })
  }

}