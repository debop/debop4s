package debop4s.web.spring.utils

import java.io.FileNotFoundException
import java.nio.file.Paths
import java.util.concurrent.Callable
import java.util.{Map => JMap}
import javax.servlet.http.HttpServletResponse

import debop4s.core._
import debop4s.core.io.FileUtils
import debop4s.web.spring.ApiResult
import org.slf4j.LoggerFactory
import org.springframework.http.{HttpStatus, ResponseEntity}
import org.springframework.web.context.request.async.WebAsyncTask

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.control.NonFatal

/**
 * Spring MVC Controller 에서 사용할 수 있는 Helper class 입니다.
 * @author Sunghyouk Bae
 */
object ControllerUtil {

  private val log = LoggerFactory.getLogger(getClass)

  val timeout: Long = 90000L

  /**
   * 지정한 코드 블럭을 Spring MVC 에서 제공하는 비동기 방식으로 작업을 처리합니다.
   * @param block 실행할 코드 블럭
   * @tparam T  리턴 값 수형
   */
  def callAsync[T](block: => T): WebAsyncTask[ResponseEntity[T]] = {
    callAsync(timeout)(block)
  }

  /**
   * 지정한 코드 블럭을 Spring MVC 에서 제공하는 비동기 방식으로 작업을 처리합니다.
   * @param timeout 제한 시간 (90초)
   * @param block 실행할 코드 블럭
   * @tparam T  리턴 값 수형
   */
  def callAsync[T](timeout: Long)(block: => T): WebAsyncTask[ResponseEntity[T]] = {
    new WebAsyncTask[ResponseEntity[T]](timeout, new Callable[ResponseEntity[T]] {
      override def call(): ResponseEntity[T] = {
        try {
          success(block)
        } catch {
          case NonFatal(e) =>
            log.error("메소드 실행에 예외가 발생했습니다.", e)
            serviceUnavailable()
        }
      }
    })
  }

  /**
   * `callable` 을 Spring MVC 에서 제공하는 비동기 방식으로 작업을 처리합니다.
   * @param callable 실행할 callable instance
   * @tparam T callable의 리턴 값의 수형
   */
  def callAsync[T](callable: Callable[T]): WebAsyncTask[ResponseEntity[T]] = {
    callAsync(timeout, callable)
  }

  /**
   * `callable` 을 Spring MVC 에서 제공하는 비동기 방식으로 작업을 처리합니다.
   * @param timeout 제한 시간 (90초)
   * @param callable 실행할 callable instance
   * @tparam T callable의 리턴 값의 수형
   */
  def callAsync[T](timeout: Long, callable: Callable[T]): WebAsyncTask[ResponseEntity[T]] = {
    new WebAsyncTask[ResponseEntity[T]](timeout, new Callable[ResponseEntity[T]] {
      override def call(): ResponseEntity[T] = {
        try {
          success(callable.call())
        } catch {
          case e: Throwable =>
            log.error("메소드 실행에 예외가 발생했습니다.", e)
            serviceUnavailable()
        }
      }
    })
  }

  /**
   * 성공을 나타내는 `ResponseEntity` 를 빌드합니다.
   * @param body 본문 내용
   */
  def success[T](body: T): ResponseEntity[T] =
    new ResponseEntity[T](body, HttpStatus.OK)

  /**
   * Service Unable 를 나타내는 `ResponseEntity`를 빌드합니다.
   */
  def serviceUnavailable[T](): ResponseEntity[T] = {
    new ResponseEntity[T](null.asInstanceOf[T], HttpStatus.SERVICE_UNAVAILABLE)
  }

  /**
   * 예외가 발생했을 시에 예외정보를 포함하는 `ResponseEntity`를 빌드합니다.
   * @param ex 예외정보
   */
  def handleException(ex: Exception): ResponseEntity[ApiResult] = {
    val result =
      if (ex != null) {
        log.error("예외 발생.", ex)
        ApiResult(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage)
      } else {
        ApiResult(999, "알 수 없는 예외가 발생했습니다.")
      }
    new ResponseEntity[ApiResult](result, HttpStatus.OK)
  }

  /**
   * 지정한 파일을 다운로드 합니다.
   * @param response    Http Response
   * @param filePath    다운로드 할 파일 경로
   * @param contentType 파일의 content type
   * @return 파일 내용
   */
  def handleFileDownload(response: HttpServletResponse,
                         filePath: String,
                         contentType: String = "application/octet-stream"): Array[Byte] = {
    val path = Paths.get(filePath)

    if (!FileUtils.exists(path))
      throw new FileNotFoundException(s"파일을 찾을 수 없습니다. path=$filePath")

    val future = FileUtils.readAllBytesAsync(path)

    future onSuccess {
      case bytes: Array[Byte] =>
        response.setHeader("Content-Disposition", "attachment; filename=\"" + path.getFileName + "\"")
        response.setContentLength(bytes.length)
        response.setContentType(contentType)
      // 이 함수가 반환하는 byte array 를 다룬다.
      //response.getOutputStream.write(bytes)
    }

    future.await
  }
}
