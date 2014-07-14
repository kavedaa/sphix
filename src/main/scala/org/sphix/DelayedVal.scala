package org.sphix

import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.Future
import javafx.application.Platform
import java.util.concurrent.TimeUnit
import javafx.beans.InvalidationListener
import java.util.concurrent.ThreadFactory

class DelayedVal[A](source: Val[A], delayTime0: Long, timeUnit: TimeUnit) extends LazyVal[A] {

  val delayTime = Var(delayTime0)
  
  def compute = source()
  
  val THREAD_FACTORY = new ThreadFactory {
    def newThread(runnable: Runnable) = {
      val th = new Thread(runnable)
      th setPriority Thread.MIN_PRIORITY
      th setDaemon true
      th
    }
  }

  val executor = new ScheduledThreadPoolExecutor(1, THREAD_FACTORY)
  var future: Option[Future[_]] = None

  val invalidator = new Runnable {
    def run() {
      runLater {
        invalidate(source)
      }
    }
  }

  (source, delayTime) observe {
    future foreach (_ cancel false)
    future = Some(executor schedule (invalidator, delayTime(), timeUnit))
  }

  //	so that we can override it in tests
  def runLater[U](r: => U) {
    Platform runLater new Runnable {
      def run() { r }
    }
  }
}