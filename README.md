Handler Extension
=================
*A small addon that adds useful methods and functions for working with `android.os.Handler`*

This package provides some functions and method to reduce the code needed to post, send, and
process Messages using the Handler class provided by the Android framework.

Sample
------

```scala
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
```

The code above produces this output:

```
I/HandlerExtensionMain(17862): Starting onCreate method
I/HandlerExtensionMain(17862): Exiting onCreate method
I/HandlerExtensionMain(17862): Ordinary post to Background
I/HandlerExtensionMain(17862): Message received on Background: Ordinary send to Background
I/HandlerExtensionMain(17862): Ordinary post to UI
I/HandlerExtensionMain(17862): Message received on UI: Ordinary send to UI
I/HandlerExtensionMain(17862): Delayed post on UI
I/HandlerExtensionMain(17862): Delayed post to Background
I/HandlerExtensionMain(17862): Message received on UI: Delayed send to UI
I/HandlerExtensionMain(17862): Message received on Background: Delayed send to Background
```

Installation
------------

No, it's not on Maven Central. Take the file `app/libs/library.aar`, maybe rename it to `handlerextension.aar`, and put
it in your project's libs folder. Modify your build.gradle like such:

```Groovy
repositories {
    flatDir {
        dirs 'libs'
    }
}
dependencies {
    compile(name:'handlerextension', ext:'aar')
    // Other dependencies
}
```
