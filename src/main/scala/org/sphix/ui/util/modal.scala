package org.sphix.ui.util

import scala.util._
import scala.concurrent._

import javafx.stage._

import org.sphix.concurrent.fxt


class WaitModal(popup: Popup) {

  def apply[A, U](future: Future[A])(f: Try[A] => U)(implicit exec: ExecutionContext, window: Window): Unit = {

    popup.show(window)

    future andThen { 
      case _ => 
        fxt { 
          popup.hide() 
        } 
    } onComplete { x => fxt { f(x) } }
  }
}