package org.sphix.crud

import javafx.scene.control.{ Alert, ButtonType }

import Alert.AlertType

class Confirmation(implicit texts: CrudTexts) {

  def confirmDelete[A](items: Seq[A])(f: A => String): Boolean =
    if (items.size == 1) confirmDelete(f(items.head)) else confirmDelete(items.size)

  def confirmDelete(item: String) = confirm(texts.ConfirmDeletion, texts.AskToDelete(item))
  def confirmDelete(num: Int) = confirm(texts.ConfirmDeletion, texts.AskToDelete(texts.NumItems(num)))

  def confirm(header: String, text: String): Boolean = 
    showAlert(AlertType.CONFIRMATION, Some(texts.Confirm), Some(header), Some(text))
      .filter(_ == ButtonType.OK)
      .isPresent

  def showAlert(alertType: AlertType, title: Option[String], header: Option[String], details: Option[String]) = {
    val alert = new Alert(alertType) {
      title foreach setTitle
      header foreach setHeaderText
      details foreach setContentText
    } 
    alert.showAndWait()
  }
}

