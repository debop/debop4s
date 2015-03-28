package debop4s.core.jvm

import scala.concurrent.duration.Duration


/**
 * Support for heapster profiling (google perftools compatible):
 *
 * https://github.com/mariusaeriksen/heapster
 */
class Heapster(klass: Class[_]) {
  private val startM = klass.getDeclaredMethod("start")
  private val stopM = klass.getDeclaredMethod("stop")
  private val dumpProfileM =
    klass.getDeclaredMethod("dumpProfile", classOf[java.lang.Boolean])
  private val clearProfileM = klass.getDeclaredMethod("clearProfile")
  private val setSamplingPeriodM =
    klass.getDeclaredMethod("setSamplingPeriod", classOf[java.lang.Integer])

  def start() { startM.invoke(null) }
  def shutdown() { stopM.invoke(null) }
  def setSamplingPeriod(period: java.lang.Integer) { setSamplingPeriodM.invoke(null, period) }
  def clearProfile() { clearProfileM.invoke(null) }
  def dumpProfile(forceGC: java.lang.Boolean): Array[Byte] =
    dumpProfileM.invoke(null, forceGC).asInstanceOf[Array[Byte]]

  def profile(howlong: Duration, samplingPeriod: Int = 10 << 19, forceGC: Boolean = true) = {
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
    val loader = ClassLoader.getSystemClassLoader
    try {
      Some(new Heapster(loader.loadClass("Heapster")))
    } catch {
      case _: ClassNotFoundException => None
    }
  }
}

