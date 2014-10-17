package com.cengallut.handlerextension

import scala.ref.WeakReference
import android.os.{Message, Handler}

/**
 * A global event hub that allows different components of
 * the app to communicate without explicit coupling.
 */
object MessageHub {

  /**
   * Subscribe to the global event stream
   */
  case class Subscribe(who: Handler)

  /**
   * Unsubscribe from the global event stream
   */
  case class Unsubscribe(who: Handler)

  def create: Handler with HandlerExt = new MessageHub

}

private final class MessageHub extends Handler with HandlerExt {
  import MessageHub._
  import HandlerExtensionPackage._

  private var subscribers = Set.empty[WeakReference[Handler]]

  override private[handlerextension] def basis: Handler = this

  override def handleMessage(msg: Message): Unit = {
    msg.obj match {
      case Subscribe(who)   => subscribers += new WeakReference[Handler](who)
      case Unsubscribe(who) => purgeSubscribers(who)
      case a: AnyRef        => broadcastEvent(a)
    }
  }

  private def purgeSubscribers(who: Handler): Unit = {
    subscribers = subscribers.filter { handlerRef =>
      handlerRef.get.isDefined && (handlerRef() ne who)
    }
  }

  private def broadcastEvent(event: AnyRef): Unit = {
    for (
      weakRefToHandler <- subscribers;
      maybeHandler     <- weakRefToHandler.get
    ) {
      maybeHandler.send(event)
    }
  }

}