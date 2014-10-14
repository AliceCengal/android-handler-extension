package com.cengallut.handlerextexample

import android.app.Activity
import android.os.{HandlerThread, Bundle}
import android.util.Log

class Main extends Activity {
  import Main.log

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

    h1.postNow {
      log("Ordinary post to Background")
    }

    h1.postDelayed(5000) {
      log("Delayed post to Background")
    }

    h2.postNow {
      log("Ordinary post to UI")
    }

    h2.postDelayed(5000) {
      log("Delayed post on UI")
    }

    h1.send("Ordinary send to Background")
    h1.sendDelayed(10000, "Delayed send to Background")

    h2.send("Ordinary send to UI")
    h2.sendDelayed(10000, "Delayed send to UI")

    log("Exiting onCreate method")

  }
}

object Main {

  def log(s: String): Unit = Log.i("HandlerExtensionMain", s)

}