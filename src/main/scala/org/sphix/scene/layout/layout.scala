package org.sphix.scene.layout

import javafx.scene.Node
import javafx.geometry.Insets
import javafx.geometry.Pos
import org.sphix.collection.ObservableSeq

class HBox(spacing: Double, padding: Double)(children: Node*) extends javafx.scene.layout.HBox {

  def this(spacing: Double)(children: Node*) = this(spacing, 0.0)(children: _*)
  def this(children: Node*) = this(0.0, 0.0)(children: _*)
  
  setSpacing(spacing)
  setPadding(new Insets(padding))
  
  setAlignment(Pos.CENTER)
  
  getChildren addAll children.to(ObservableSeq)
}