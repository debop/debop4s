import debop4s.web.scalatra.TomcatLauncherSupport

/**
 * Embedded Tomcat 를 실행시키는 Object 입니다.
 *
 * @author sunghyouk.bae@gmail.com
 */
object TomcatLauncher extends TomcatLauncherSupport {

  // 테스트 용
  override def resourceBase: String = "src/test/webapp"

}
