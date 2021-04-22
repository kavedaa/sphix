package org.sphix.crud

import scala.util._

import org.controlsfx.control.Notifications

import org.sphix.ui._

 class Feedback(implicit texts: CrudTexts) {

  def error(ex: Throwable) = {
    val actual = Option(ex.getCause) getOrElse ex
    (new ErrorDialog(texts.Error, actual.getMessage)).showAndWait()
  }

  def notification(msg: String) = Notifications.create.text(msg).showInformation()

  def created[A](item: Try[A])(render: A => String) = 
    item match {
      case Success(x) =>
        notification(s"${texts.Created} ${render(x)}.")
      case Failure(ex) =>
        error(ex)
    }

  def updated[A](item: Try[A])(render: A => String) = 
    item match {
      case Success(x) =>
        notification(s"${texts.Updated} ${render(x)}.")
      case Failure(ex) =>
        error(ex)
    }

  def deleted[A](items: Seq[(A, Try[_])])(render: A => String) = {
    val failures = items collect { case (item, f @ Failure(_)) => (item, f) }
    if (failures.nonEmpty) (new ErrorsDialog(texts.Error, failures)(render)).showAndWait()
  }
}