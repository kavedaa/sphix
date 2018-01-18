package org.sphix

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try
import org.sphix.concurrent.fxt

case class FutureVal[A](future: Future[A])(implicit executor: ExecutionContext)
  extends FirableVal[Option[Try[A]]] with ValImpl[Option[Try[A]]] {

  //  we don't really need this here, there will only ever be one invalidation
  def currentValue = None

  def getValue = future.value

  future onComplete {
    case _ => fxt { fire() }
  }
}
