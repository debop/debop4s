package com.github.debop4s.core.utils

import com.github.debop4s.core.Guard
import java.io.InputStream
import org.slf4j.LoggerFactory

/**
 * com.github.debop4s.core.tools.Resources
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 13. 오후 7:52
 */
object Resources {

  private lazy val log = LoggerFactory.getLogger(getClass)

  /**
   * 지정한 경로의 리소스를 읽기위한 InputStream 을 반환합니다.
   *
   * @param path 경로
   * @return 리소스의 Input Stream. 해당 리소스가 없다면 null 을 반환한다.
   */
  def getClassPathResourceStream(path: String): InputStream = {
    log.debug(s"리소스 파일을 읽습니다. path=[$path]")
    Guard.shouldNotBeEmpty(path, "path")

    val url = if (path.startsWith("/")) path.substring(1) else path
    getClass.getClassLoader.getResourceAsStream(url)
  }

  /**
   * 지정한 경로의 리소스를 읽기위한 InputStream 을 반환합니다.
   *
   * @param path 경로
   * @param classLoader 리소스 로더
   * @return 리소스의 Input Stream. 해당 리소스가 없다면 null 을 반환한다.
   */
  def getClassPathResourceStream(path: String, classLoader: ClassLoader): InputStream = {
    log.debug(s"리소스 파일을 읽습니다. path=[$path]")
    Guard.shouldNotBeEmpty(path, "path")
    Guard.shouldNotBeNull(classLoader, "classLoader")

    val url = if (path.startsWith("/")) path.substring(1) else path
    classLoader.getResourceAsStream(url)
  }

}
