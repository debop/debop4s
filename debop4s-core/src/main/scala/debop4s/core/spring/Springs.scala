package debop4s.core.spring

import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationContext
import org.springframework.context.support.GenericApplicationContext
import scala.annotation.varargs

/**
 * debop4s.core.spring.Springs
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 11. 오후 5:26
 */
object Springs {

  private lazy val log = LoggerFactory.getLogger(getClass)

  private var globalContext: ApplicationContext = null
  private val NOT_INITIALIZED_MSG = "Springs의 ApplicationContext가 초기화되지 않았습니다. Springs를 ComponentScan 해주셔야합니다!!!"

  private def assertInitialized() {
    assert(isInitialized, NOT_INITIALIZED_MSG)
  }

  def getContext = {
    assertInitialized()
    globalContext.asInstanceOf[GenericApplicationContext]
  }

  def isInitialized = synchronized {
    globalContext != null
  }

  def initialize(ctx: ApplicationContext) {
    require(ctx != null, "application context is null")
    log.info("Spring ApplicationContext를 지정합니다.")

    if (globalContext != null)
      log.warn("ApplicationContext가 이미 지정되었으므로, 무시합니다. reset 후 initialize를 호출하세요.")

    synchronized {
      globalContext = ctx
    }
    log.info("Spring ApplicationContext를 지정했습니다.")
  }

  def reset(contextToReset: ApplicationContext = null): Boolean = {
    synchronized {
      if (contextToReset == null) {
        globalContext = null
        log.info("Spring ApplicationContext를 초기화했습니다.")
        return true
      } else if (contextToReset == globalContext) {
        globalContext = null
        log.info("Spring ApplicationContext를 초기화했습니다.")
        return true
      }

      return true
    }
  }

  @varargs
  def getBean[T](name: String, args: Any*): T = {
    getContext.getBean(name, args.map(_.asInstanceOf[AnyRef]): _*).asInstanceOf[T]
  }

  def getBean[T](requiredType: Class[T]): T =
    getContext.getBean[T](requiredType)


  @varargs
  def tryGetBean[T](name: String, args: Any*): T = {
    try {
      return getContext.getBean(name, args.map(_.asInstanceOf[AnyRef]): _*).asInstanceOf[T]
    } catch {
      case e: Throwable =>
        log.warn(s"Bean을 찾는데 실패했습니다. null을 반환합니다. bean name=[$name]", e)
    }
    null.asInstanceOf[T]
  }

  def tryGetBean[T <: AnyRef](requiredType: Class[T]): T = {
    try {
      return getContext.getBean[T](requiredType)
    } catch {
      case e: Throwable =>
        log.warn(s"Bean을 찾는데 실패했습니다. null을 반환합니다. required type=[$requiredType]", e)
    }
    null.asInstanceOf[T]
  }
}
