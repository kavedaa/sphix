package org.sphix.controlsfx

import java.util.function.Consumer
import javafx.event.ActionEvent
import javafx.scene.Node

class Action(text: String) extends org.controlsfx.control.action.Action(text) {

  def setAction[U](block: => U) = setEventHandler {
    new Consumer[ActionEvent] {
      def accept(e: ActionEvent) = block
    }
  }
}

object Action {

  def apply(text: String, graphic: Node) =
    new Action(text) {
      setGraphic(graphic)
    }
}
