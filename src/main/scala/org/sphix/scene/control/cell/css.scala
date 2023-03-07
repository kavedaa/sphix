package org.sphix.scene.control.cell

import javafx.scene.control.TableCell

trait CSSTableCell[S, T] extends TableCell[S, T] {

  def cssClasses: Seq[(String, S => Boolean)]

  override def updateItem(item: T, empty: Boolean) = {
    super.updateItem(item, empty)
    cssClasses foreach { case (name, condition) => getStyleClass remove name }
    if (!empty) {
      cssClasses foreach {
        case (name, condition) =>
          if (condition(getTableView.getItems get getIndex)) getStyleClass add name
      }
    }
  }

}
