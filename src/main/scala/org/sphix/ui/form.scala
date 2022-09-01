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
  val padding = 10

  def vbox(nodes: Node*) = new VBox(boxGap) {
    getChildren.addAll(nodes: _*)
  }

  def pvbox(nodes: Node*) = new VBox(boxGap) {
    getChildren.addAll(nodes: _*)
    setPadding(new Insets(padding))
  }

  def hbox(nodes: Node*) = new HBox(boxGap) {
    getChildren.addAll(nodes: _*)
    setAlignment(Pos.CENTER_LEFT)
  }

  def phbox(nodes: Node*) = new HBox(boxGap) {
    getChildren.addAll(nodes: _*)
    setPadding(new Insets(padding))
    setAlignment(Pos.CENTER_LEFT)
  }

  def velem(label: String, nodes: Node*) =
    new VBox(elemGap) {
      getChildren.add(new Label(label))
      getChildren.addAll(nodes: _*)
    }

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

  def helem(label: String, nodes: Node*) =
    new HBox(elemGap) {
      getChildren.add(new Label(label))
      getChildren.addAll(nodes: _*)
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

  def grid2(items: Seq[(Node, Node)]) = new GridPane {

    items.zipWithIndex foreach { case ((elem1, elem2), index) =>
      add(elem1, 0, index)
      add(elem2, 1, index)
    }

    setHgap(elemGap)
    setVgap(elemGap)
    setPadding(new Insets(boxGap))
  }

  def grid3(items: Seq[(Node, Node, Node)]) = new GridPane {

    items.zipWithIndex foreach { case ((elem1, elem2, elem3), index) =>
      add(elem1, 0, index)
      add(elem2, 1, index)
      add(elem3, 2, index)
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
