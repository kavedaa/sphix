package org.sphix.crud

import scala.jdk.CollectionConverters._

import javafx.scene.layout._
import javafx.scene.control._
import javafx.scene.input._
import javafx.scene.image._

import org.controlsfx.control.action._

import org.sphix.collection._
import org.sphix.Var._
import org.sphix.Val._
import java.awt.Cursor

sealed trait CrudOperation
object CrudOperation {
  object Add extends CrudOperation
  object Edit extends CrudOperation
  object Delete extends CrudOperation
}

abstract class CrudPane(
  operations: Seq[CrudOperation],
  add: => Unit,
  edit: => Unit,
  delete: => Unit)
  (implicit texts: CrudTexts,
  icons: CrudIcons)
  extends BorderPane {

  val addAction = new Action(texts.Add + "...", _ => add) { setGraphic(new ImageView(icons.Add)) }
  val editAction = new Action(texts.Edit + "...", _ => edit) { setGraphic(new ImageView(icons.Edit)) }
  val deleteAction = new Action(texts.Delete, _ => delete) { setGraphic(new ImageView(icons.Delete)) }

  val toolbarButtons = Seq(
    Option.when(operations contains CrudOperation.Edit)(editAction),
    Option.when(operations contains CrudOperation.Delete)(deleteAction)
  ).flatten

  val toolbar = ActionUtils.createToolBar(toolbarButtons.asJava, ActionUtils.ActionTextBehavior.SHOW)

  if (operations contains CrudOperation.Add) {
    toolbar.getItems.addAll(0, Seq(ActionUtils.createButton(addAction), new Separator).asJava)
  }

  def contextActions: Seq[Action]

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

    val contextMenu = ActionUtils.createContextMenu(contextActions.to(ObservableSeq))

    table.setContextMenu(contextMenu)

    contextActions foreach { action =>
      action.disabledProperty <== table.getSelectionModel.selectedIndexProperty map { index =>
        index == -1
      }
    }

    setTop(toolbar)
    setCenter(table)
  }

}