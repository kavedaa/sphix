package org.sphix.util

import javafx.scene.image.Image

trait ImageResolver extends (String => Image)

class ResourceImageResolver(cls: Class[_], f: String => String) extends ImageResolver {
  def apply(filename: String) = new Image(cls getResourceAsStream f(filename))
}