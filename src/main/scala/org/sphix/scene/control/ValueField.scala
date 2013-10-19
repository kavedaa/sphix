package org.sphix.scene.control

import org.sphix.Var
import org.sphix.util.RightConverter
import Var._

class ValueField[A](converter: RightConverter[A, String]) extends javafx.scene.control.TextField {

  def getConvertedText = converter deconvert getText

  val value = Var(getConvertedText)

  value onValue { v => noloop { setText(v map (converter convert _) getOrElse "") } }

  textProperty onValue { t => noloop { value() = getConvertedText } }

  var updating = false

  private def noloop[U](u: => U) = if (!updating) { updating = true; u; updating = false }

}