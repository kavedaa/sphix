package org.sphix

import javafx.scene.image.ImageView
import javafx.scene.image.Image
import javafx.event.EventHandler
import javafx.event.Event
import javafx.util.Callback
import java.util.function.Consumer
import java.lang.Thread.UncaughtExceptionHandler

package object util {

  implicit def runnable[U](r: () => U) = new Runnable {
    def run() { r() }
  }

  implicit def eventHandler[E <: Event, U](h: E => U) = new EventHandler[E] {
    def handle(e: E) { h(e) }
  }

  implicit def eventHandler0[E <: Event, U](h: () => U) = new EventHandler[E] {
    def handle(e: E) { h() }
  }

  implicit def stringResourceToImage(s: String)(implicit resolver: ImageResolver) =
    resolver(s)

  implicit def stringResourceToImageView(s: String)(implicit resolver: ImageResolver) =
    new ImageView(resolver(s))

  implicit def imageToImageView(i: Image) = new ImageView(i)
  
  implicit def callback[A, B](f: A => B) = new Callback[A, B] {
    def call(a: A) = f(a)
  }

  implicit def consumer[A](f: A => Unit) = new Consumer[A] {
    def accept(a: A) = f(a)
  }
  
  implicit def uncaughtExceptionHandler(f: (Thread, Throwable) => Unit) = new UncaughtExceptionHandler {
    def uncaughtException(thread: Thread, throwable: Throwable) = f(thread, throwable)
  }

}