package debop4s.core.jvm

import java.lang.reflect.Method

import scala.concurrent.duration.Duration


/**
 * Support for heapster profiling (google perftools compatible):
 *
 * https://github.com/mariusaeriksen/heapster
 */
class Heapster(klass: Class[_]) {

  private[this] val startM: Method =
    klass.getDeclaredMethod("start")

  private[this] val stopM: Method =
    klass.getDeclaredMethod("stop")

  private[this] val dumpProfileM: Method =
    klass.getDeclaredMethod("dumpProfile", classOf[java.lang.Boolean])

  private[this] val clearProfileM: Method =
    klass.getDeclaredMethod("clearProfile")

  private[this] val setSamplingPeriodM: Method =
    klass.getDeclaredMethod("setSamplingPeriod", classOf[java.lang.Integer])

  def start(): Unit = { startM.invoke(null) }

  def shutdown(): Unit = { stopM.invoke(null) }

  def setSamplingPeriod(period: java.lang.Integer): Unit =
    setSamplingPeriodM.invoke(null, period)

  def clearProfile(): Unit =
    clearProfileM.invoke(null)

  def dumpProfile(forceGC: java.lang.Boolean): Array[Byte] =
    dumpProfileM.invoke(null, forceGC).asInstanceOf[Array[Byte]]

  def profile(howlong: Duration, samplingPeriod: Int = 10 << 19, forceGC: Boolean = true): Array[Byte] = {
    clearProfile()
    setSamplingPeriod(samplingPeriod)

    start()
    Thread.sleep(howlong.toMillis)
    shutdown()
    dumpProfile(forceGC)
  }
}

object Heapster {
  val instance: Option[Heapster] = {
    val loader: ClassLoader = ClassLoader.getSystemClassLoader
    try {
      Some(new Heapster(loader.loadClass("Heapster")))
    } catch {
      case _: ClassNotFoundException => None
    }
  }
}

