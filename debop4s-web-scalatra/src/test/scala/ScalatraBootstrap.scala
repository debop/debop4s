import debop4s.web.scalatra.SpringScalatraBootstrap
import debop4s.web.scalatra.config.SpringConfiguration

/**
 * ScalatraBootstrap
 * @author sunghyouk.bae@gmail.com
 */
class ScalatraBootstrap extends SpringScalatraBootstrap {
  override protected def configClasses(): Array[Class[_]] = {
    Array(classOf[SpringConfiguration])
  }
  //  override protected def basePackages(): Array[String] = {
  //    Array("kr.hconnect.scalatra")
  //  }
}
