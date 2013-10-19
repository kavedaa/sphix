package org.sphix

import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.Future
import javafx.application.Platform
import java.util.concurrent.TimeUnit
import javafx.beans.InvalidationListener
import java.util.concurrent.ThreadFactory

class DelayedVal[A](source: Val[A], millis: Long) extends FirableVal[A] {

  private var value: A = source()

  def getValue = value

  private var sourceValue: A = _

  def currentValue = value
  
  val THREAD_FACTORY = new ThreadFactory {
    def newThread(run: Runnable) = {
      val th = new Thread(run);
      //                    th.setUncaughtExceptionHandler(UNCAUGHT_HANDLER);
      th.setPriority(Thread.MIN_PRIORITY);
      th.setDaemon(true);
      th
    }
  }

  val executor = new ScheduledThreadPoolExecutor(1, THREAD_FACTORY)
  var future: Option[Future[_]] = None

  val propagator = new Runnable {
    def run() {
      Platform runLater new Runnable {
        def run() {
          value = sourceValue
          fire()
        }
      }
    }
  }

  source onValue { v =>
    sourceValue = v
    future foreach (_ cancel false)
    future = Some(executor schedule (propagator, millis, TimeUnit.MILLISECONDS))
  }
}