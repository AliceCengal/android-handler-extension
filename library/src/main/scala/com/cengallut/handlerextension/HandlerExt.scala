package com.cengallut.handlerextension

import android.os.{Handler, Looper, Message}

/**
 * Add this trait to the package object or Activity to enable Handler extension.
 */
trait HandlerExtensionPackage {

  /**
   * A quick and easy way to define a Handler just by providing
   * a partial function to handle messages. The messages will be
   * processed on the thread where this Handler is created.
   *
   * @param pf A partial function to handle incoming messages.
   * @return A Handler that will execute the partial function for
   *         every messages received.
   */
  def handler()(pf: PartialFunction[AnyRef, Unit]): Handler =
    new PartialFuncHandler(pf)

  /**
   * Same as `handler(pf)` except that the HAndler will process the
   * message on the thread associated with the given looper.
   *
   * @param looper Looper of the thread where the Handler will process
   *               its incoming messages
   * @param pf A partial function to handle incoming messages.
   * @return A Handler that will execute the partial function for
   *         every messages received.
   */
  def handler(looper: Looper)(pf: PartialFunction[AnyRef, Unit]): Handler =
    new PartialFuncLooperHandler(pf, looper)

  /**
   * Implicitly convert normal Handler to enable extension methods.
   *
   * @param h The normal Handler to be extended.
   * @return A shell which implements all the extensions methods.
   */
  implicit def handler2actorConversion(h: Handler): HandlerExt =
    new HandlerShell(h)

  /**
   * @return A normal that runs on the main UI thread.
   */
  def uiHandler = new Handler(Looper.getMainLooper)

  /**
   * Same as `handler(pf)` except that the Handler returned runs on
   * the UI thread.
   *
   * @param pf A partial function to handle incoming messages.
   * @return A Handler that will execute the partial function for
   *         every messages received.
   */
  def uiHandler(pf: PartialFunction[AnyRef, Unit]): Handler =
    new PartialFuncLooperHandler(pf, Looper.getMainLooper)

}

/**
 * Extension methods.
 */
trait HandlerExt {

  private[handlerextension] def basis: Handler

  def postNow(task: => Unit): Unit = {
    basis.post(new ClosureRunnable(task))
  }

  def postDelayed(delayMillis: Long)(task: => Unit): Unit = {
    basis.postDelayed(new ClosureRunnable(task), delayMillis)
  }

  def send(msg: AnyRef): Unit = {
    basis.sendMessage(basis.obtainMessage(0, msg))
  }

  def sendDelayed(delayMillis: Long, msg: AnyRef): Unit = {
    basis.sendMessageDelayed(basis.obtainMessage(0, msg), delayMillis)
  }

}

private[handlerextension] class HandlerShell(h: Handler) extends HandlerExt {

  def basis = h

}

private[handlerextension] class PartialFuncHandler(pf: PartialFunction[AnyRef, Unit])
  extends Handler with HandlerExt {

  override def basis = this

  override def handleMessage(msg: Message): Unit = {
    if (pf.isDefinedAt(msg.obj)) pf(msg.obj)
  }

}

private[handlerextension] class PartialFuncLooperHandler(pf: PartialFunction[AnyRef, Unit],
                                                         looper: Looper)
  extends Handler(looper) with HandlerExt {

  override def basis: Handler = this

  override def handleMessage(msg: Message): Unit = {
    if (pf.isDefinedAt(msg.obj)) pf(msg.obj)
  }

}

private[handlerextension] class ClosureRunnable(closure: => Unit) extends Runnable {
  override def run(): Unit = closure
}
