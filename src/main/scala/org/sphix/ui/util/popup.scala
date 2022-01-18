package org.sphix.ui.util

import javafx.stage.Popup
import javafx.scene.control._
import javafx.scene.layout._
import javafx.geometry.Pos
import javafx.scene.shape.Rectangle
import javafx.scene.paint.Color
import javafx.scene.effect.DropShadow

class SpinnerPopup(message: String) extends Popup {

  val label = new Label(message)

  val spinner = new ProgressIndicator {
    setPrefSize(75, 75)
  }
  val pane = new StackPane {
    val vb = new VBox(10) {
      setAlignment(Pos.CENTER)
      getChildren addAll(spinner, label)
    }
    getChildren addAll(new Rectangle(150, 150, Color.WHITE), vb)
    setEffect(new DropShadow)
  }
  getContent add pane
}
