package debop4s.core.utils

import java.io.InputStream

import debop4s.core._

/**
 * debop4s.core.tools.Resources
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 13. 오후 7:52
 */
object Resources extends Logging {

  /**
   * 지정한 경로의 리소스를 읽기위한 InputStream 을 반환합니다.
   *
   * @param path 경로
   * @return 리소스의 Input Stream. 해당 리소스가 없다면 null 을 반환한다.
   */
  def getClassPathResourceStream(path: String): InputStream = {
    require(Strings.isNotEmpty(path))
    debug(s"리소스 파일을 읽습니다. path=$path")

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
    require(Strings.isNotEmpty(path))
    require(classLoader != null)
    debug(s"리소스 파일을 읽습니다. path=[$path]")

    val url = if (path.startsWith("/")) path.substring(1) else path
    classLoader.getResourceAsStream(url)
  }

  /**
   * 지정한 경로의 리소스를 읽어 문자열로 반환합니다.
   * @param path  리소스 경로
   * @return 리소스의 문자열, 없으면 빈문자열을 반환합니다.
   */
  def getString(path: String): String = {
    require(Strings.isNotEmpty(path))
    debug(s"read resource string. path=$path")

    using(getClassPathResourceStream(path)) { is =>
      Streams.toString(is)
    }
  }

  /**
   * 지정한 경로의 리소스를 읽어 문자열로 반환합니다.
   * @param path  리소스 경로
   * @param classLoader 리소스 로더
   * @return 리소스의 문자열, 없으면 빈문자열을 반환합니다.
   */
  def getString(path: String, classLoader: ClassLoader): String = {
    require(Strings.isNotEmpty(path))
    debug(s"read resource string. path=$path, classLoader=$classLoader")

    using(getClassPathResourceStream(path, classLoader)) { is =>
      Streams.toString(is)
    }
  }

  /**
   * 지정한 경로의 리소스를 읽어 바이트 배열로 반환합니다.
   * @param path  리소스 경로
   * @return 리소스의 문자열, 없으면 빈문자열을 반환합니다.
   */
  def getBytes(path: String): Array[Byte] = {
    require(Strings.isNotEmpty(path))
    debug(s"load resource bytes. path=$path")

    using(getClassPathResourceStream(path)) { is =>
      Streams.toByteArray(is)
    }
  }

  /**
   * 지정한 경로의 리소스를 읽어 바이트 배열로 반환합니다.
   * @param path  리소스 경로
   * @param classLoader 리소스 로더
   * @return 리소스의 문자열, 없으면 빈문자열을 반환합니다.
   */
  def getBytes(path: String, classLoader: ClassLoader): Array[Byte] = {
    require(Strings.isNotEmpty(path))
    debug(s"load resource bytes. path=$path, classLoader=$classLoader")

    using(getClassPathResourceStream(path, classLoader)) { is =>
      Streams.toByteArray(is)
    }
  }

}
