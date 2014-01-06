package kr.debop4s.core.spring

import kr.debop4s.core.logging.Logger
import org.springframework.context.ApplicationContext

/**
 * kr.debop4s.core.spring.Springs
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 11. 오후 5:26
 */
object Springs {

    lazy val log = Logger(this.getClass)

    def tryGetBean(ctx: ApplicationContext, name: String, args: Any*): AnyRef = {
        try {
            return ctx.getBean(name)
        } catch {
            case e: Throwable =>
                log.warn(s"Bean을 찾는데 실패했습니다. null을 반환합니다. bean name=$name", e)
        }
        null
    }

    def tryGetBean[T <: AnyRef](ctx: ApplicationContext, requiredType: Class[T]): T = {
        try {
            return ctx.getBean[T](requiredType)
        } catch {
            case e: Throwable =>
                log.warn(s"Bean을 찾는데 실패했습니다. null을 반환합니다. required type=$requiredType", e)
        }
        null.asInstanceOf[T]
    }
}
