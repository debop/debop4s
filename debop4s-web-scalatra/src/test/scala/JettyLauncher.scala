import debop4s.web.scalatra.JettyLauncherSupport

/**
 * JettyLauncher
 * @author sunghyouk.bae@gmail.com
 */
object JettyLauncher extends JettyLauncherSupport {

  // 테스트 용
  override def resourceBase: String = "src/test/webapp"

}
