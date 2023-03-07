package org.sphix

import java.util.concurrent.LinkedBlockingQueue
import java.security.AccessController
import java.security.PrivilegedAction
import sun.util.logging.PlatformLogger
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import scala.concurrent.ExecutionContext
import javafx.application.Platform

package object concurrent {

  //	All of this taken directly from JavaFX's Service.java. 

  /**
   * Logger used in the case of some uncaught exceptions
   */
  private final val LOG = PlatformLogger getLogger "org.sphix.concurrent"

  /*
        The follow chunk of static state is for defining the default Executor used
        with the Service. This is based on pre-existing JavaFX Script code and
        experience with JavaFX Script. It was necessary to have a thread pool by default
        because we found naive code could totally overwhelm the system otherwise
        by spawning thousands of threads for fetching resources, for example.
        We also set the priority and daemon status of the thread in its thread
        factory.
     */
  private final val THREAD_POOL_SIZE = 32
  private final val THREAD_TIME_OUT = 1000L

  /**
   * Because the ThreadPoolExecutor works completely backwards from what we want (ie:
   * it doesn't increase thread count beyond the core pool size unless the queue is full),
   * our queue has to be smart in that it will REJECT an item in the queue unless the
   * thread size in the EXECUTOR is > 32, in which case we will queue up.
   */

  private final val IO_QUEUE = new LinkedBlockingQueue[Runnable] {
    override def offer(runnable: Runnable) = {
      if (EXECUTOR.getPoolSize < THREAD_POOL_SIZE) false
      else super.offer(runnable)
    }
  }

  // Addition of doPrivileged added due to RT-19580
  private final val THREAD_GROUP = AccessController doPrivileged new PrivilegedAction[ThreadGroup] {
    def run = new ThreadGroup("javafx concurrent thread pool")

  }

  private final val UNCAUGHT_HANDLER = new Thread.UncaughtExceptionHandler {
    def uncaughtException(thread: Thread, throwable: Throwable) = {
      // Ignore IllegalMonitorStateException, these are thrown from the ThreadPoolExecutor
      // when a browser navigates away from a page hosting an applet that uses
      // asynchronous tasks. These exceptions generally do not cause loss of functionality.
      if (!(throwable.isInstanceOf[IllegalMonitorStateException])) {
        LOG warning ("Uncaught throwable in " + THREAD_GROUP.getName, throwable)
      }
    }
  }

  private final val THREAD_FACTORY = new ThreadFactory {
    override def newThread(runnable: Runnable) =
      // Addition of doPrivileged added due to RT-19580
      AccessController doPrivileged new PrivilegedAction[Thread] {
        def run = {
          val th = new Thread(THREAD_GROUP, runnable)
          th setUncaughtExceptionHandler UNCAUGHT_HANDLER
          th setPriority Thread.MIN_PRIORITY
          th setDaemon true
          th
        }
      }
  }

  private val executions = Var(0)
  val executing = executions map(_ > 0)
  
  private final val EXECUTOR: ThreadPoolExecutor = new ThreadPoolExecutor(
    2, THREAD_POOL_SIZE,
    THREAD_TIME_OUT,
    TimeUnit.MILLISECONDS,
    IO_QUEUE,
    THREAD_FACTORY,
    new ThreadPoolExecutor.AbortPolicy) {
    
    allowCoreThreadTimeOut(true)
    
    override def beforeExecute(t: Thread, r: Runnable) = {
      super.beforeExecute(t, r)
      runLater { executions() = executions() + 1 }      
    }

    override def afterExecute(r: Runnable, t: Throwable) = {
      super.afterExecute(r, t)
      runLater { executions() = executions() - 1 }      
    }
    
  }

  implicit val jfxExecutionContext: ExecutionContext = ExecutionContext fromExecutor EXECUTOR
  
  def runLater[U](r: => U) = Platform runLater new Runnable { def run: Unit = { r } }  
  
  def fxt[U](r: => U) = Platform runLater new Runnable { def run: Unit = { r } }
}