package org.sphix.concurrent

import java.util.concurrent._
import javafx.concurrent.Task
import javafx.application.Platform
import org.sphix.util._

trait TaskUtil {

  //	taken straight from JavaFX' Service implementation
  
  val THREAD_POOL_SIZE = 32
  val THREAD_TIME_OUT = 1000L

  val IO_QUEUE = new LinkedBlockingQueue[Runnable]

  val THREAD_FACTORY = new ThreadFactory {
    def newThread(run: Runnable) = {
      val th = new Thread(run);
      //                    th.setUncaughtExceptionHandler(UNCAUGHT_HANDLER);
      th.setPriority(Thread.MIN_PRIORITY);
      th.setDaemon(true);
      th
    }
  }

  val EXECUTOR = new ThreadPoolExecutor(
    2, THREAD_POOL_SIZE,
    THREAD_TIME_OUT, TimeUnit.MILLISECONDS,
    IO_QUEUE, THREAD_FACTORY, new ThreadPoolExecutor.AbortPolicy()) {
    allowCoreThreadTimeOut(true)
  }

  def execute(task: Task[_]) = {
    println("executing " + task)
    EXECUTOR execute task 
  }

  def runLater[U](r: => U) = Platform runLater { () => r }  
}