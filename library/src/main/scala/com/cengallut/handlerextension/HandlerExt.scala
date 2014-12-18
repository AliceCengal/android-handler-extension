package com.cengallut.handlerextension

import android.os.{Handler, Looper, Message}
import HandlerExtensionPackage.Respond

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
  def handler()(pf: Respond): Handler with HandlerExt =
    new PartialFuncHandler(pf)

  /**
   * Same as `handler(pf)` except that the Handler will process the
   * message on the thread associated with the given looper.
   *
   * @param looper Looper of the thread where the Handler will process
   *               its incoming messages
   * @param pf A partial function to handle incoming messages.
   * @return A Handler that will execute the partial function for
   *         every messages received.
   */
  def handler(looper: Looper)(pf: Respond): Handler with HandlerExt =
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
   * @return A normal Handler that runs on the main UI thread.
   */
  def uiHandler: Handler with HandlerExt =
    new LooperHandler(Looper.getMainLooper)

  /**
   * Same as `handler(pf)` except that the Handler returned runs on
   * the UI thread.
   *
   * @param pf A partial function to handle incoming messages.
   * @return A Handler that will execute the partial function for
   *         every messages received.
   */
  def uiHandler(pf: Respond): Handler with HandlerExt =
    new PartialFuncLooperHandler(pf, Looper.getMainLooper)

}

/**
 * You can also do `import HandlerExtensionPackage._` to get the extension 
 * in a small scope.
 */
object HandlerExtensionPackage extends HandlerExtensionPackage {

  type Respond = PartialFunction[AnyRef, Unit]

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


private[handlerextension]
final class HandlerShell(h: Handler) extends HandlerExt {

  def basis = h

}

private[handlerextension]
final class PartialFuncHandler(pf: Respond)
  extends Handler with HandlerExt {

  override def basis = this

  override def handleMessage(msg: Message): Unit = {
    if (pf.isDefinedAt(msg.obj)) pf(msg.obj)
  }

}

private[handlerextension]
final class PartialFuncLooperHandler(pf: Respond, looper: Looper)
    extends Handler(looper) with HandlerExt {

  override def basis: Handler = this

  override def handleMessage(msg: Message): Unit = {
    if (pf.isDefinedAt(msg.obj)) pf(msg.obj)
  }

}

private[handlerextension]
final class LooperHandler(looper: Looper)
    extends Handler(looper) with HandlerExt {

  override private[handlerextension] def basis: Handler = this

}

private[handlerextension]
final class ClosureRunnable(closure: => Unit) extends Runnable {

  override def run(): Unit = closure

}


