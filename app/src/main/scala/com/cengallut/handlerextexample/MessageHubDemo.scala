package com.cengallut.handlerextexample

import android.app.Activity
import android.os.{HandlerThread, Bundle}
import android.util.Log
import com.cengallut.handlerextension.MessageHub

class MessageHubDemo extends Activity {
  import MessageHubDemo._

  override def onCreate(saved: Bundle): Unit = {
    super.onCreate(saved)
    setContentView(R.layout.activity_main)

    log("Starting onCreate method")

    val thread = new HandlerThread("Background thread")
    thread.start()

    val h1 = handler(thread.getLooper) {
      case s: String => log(s"Message received on Background: $s")
    }

    val h2 = uiHandler {
      case s: String => log(s"Message received on UI: $s")
    }

    val hub = MessageHub.create

    hub.send(MessageHub.Subscribe(h1))
    hub.send(MessageHub.Subscribe(h2))

    hub.send("Ordinary send to Hub")

    hub.sendDelayed(5000, "Delayed send to Hub")

    log("Exiting onCreate method")
  }

}

object MessageHubDemo {

  def log(s: String): Unit = {
    Log.i("MessageHubDemo", s)
  }

}