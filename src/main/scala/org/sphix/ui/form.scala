package org.sphix.ui

import javafx.scene.Node
import javafx.scene.layout._
import javafx.scene.control._
import javafx.geometry._

import org.sphix.Val
import org.sphix.Val._

trait FormUtils { utils =>

  val boxGap = 10
  val elemGap = 5

  def vbox(nodes: Node*) = new VBox(boxGap) {
    getChildren.addAll(nodes: _*)
  }

  def hbox(nodes: Node*) = new HBox(boxGap) {
    getChildren.addAll(nodes: _*)
    setAlignment(Pos.CENTER_LEFT)
  }

  def velem(label: String, node: Node) =
    new VBox(elemGap, new Label(label), node)

  def vvelems(elems: (String, Node)*) = {
    val nodes = elems map { case (label, node) =>
      velem(label, node)
    } 
    vbox(nodes: _*)
  }

  def hvelems(elems: (String, Node)*) = {
    val nodes = elems map { case (label, node) =>
      velem(label, node)
    } 
    hbox(nodes: _*)
  }

  def helem(label: String, node: Node) =
    new HBox(elemGap, new Label(label), node) {
      setAlignment(Pos.CENTER_LEFT)
    }

  def hhelems(elems: (String, Node)*) = {
    val nodes = elems map { case (label, node) =>
      helem(label, node)
    } 
    hbox(nodes: _*)
  }

  def vhelems(elems: (String, Node)*) = {
    val nodes = elems map { case (label, node) =>
      helem(label, node)
    } 
    vbox(nodes: _*)
  }

  def grid(items: (String, Node)*) = new GridPane {

    items.zipWithIndex foreach { case ((label, node), index) =>
      add(new Label(label), 0, index)
      add(node, 1, index)
    }

    setHgap(elemGap)
    setVgap(elemGap)
    setPadding(new Insets(boxGap))
  }


  class RadioGroup[A](items: Seq[A])(render: A => String) {
    val group = new ToggleGroup
    val itemButtons = items map { item =>
      item -> new RadioButton(render(item)) { setToggleGroup(group) }
    }
    def vbox = utils.vbox(itemButtons.map(_._2): _*)
    def hbox = utils.hbox(itemButtons.map(_._2): _*)
    val selectedItem: Val[Option[A]] = group.selectedToggleProperty map { toggle =>
      itemButtons collectFirst { case (item, button) if button eq toggle => item }
    }
  }

}
