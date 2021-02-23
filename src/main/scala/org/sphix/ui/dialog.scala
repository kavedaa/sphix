package org.sphix.ui

import javafx.scene.Node
import javafx.scene.control._
import javafx.scene.control.ButtonBar.ButtonData

import org.sphix.Val

trait DialogUtils[A >: Null] extends Dialog[A] with FormUtils {

  def title: String
  def content: Node
  def ok: String
  def valid: Val[Boolean]

  def result: Option[A]

  def init() = {

    setTitle(title)
    getDialogPane.setContent(content)

    val executeButtonType = new ButtonType(ok, ButtonData.OK_DONE)
  
    getDialogPane.getButtonTypes.addAll(executeButtonType)

    val executeButton = (getDialogPane lookupButton executeButtonType).asInstanceOf[Button]
    executeButton.disableProperty bind valid.map(x => !x)

    setResultConverter { (dialogButtonType: ButtonType) =>
      if ((dialogButtonType == executeButtonType) && valid()) result.orNull
      else null
    }
  }
}