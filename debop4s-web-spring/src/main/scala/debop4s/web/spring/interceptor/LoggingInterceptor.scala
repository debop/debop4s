package debop4s.web.spring.interceptor

import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

import org.aspectj.lang.annotation.Aspect
import org.slf4j.LoggerFactory
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter

/**
 * Spring MVC에서 사용할 Logging Interceptor 입니다.
 * @author Sunghyouk Bae
 */
@Aspect
class LoggingInterceptor extends HandlerInterceptorAdapter {

  private val log = LoggerFactory.getLogger(getClass)

  override def preHandle(request: HttpServletRequest,
                         response: HttpServletResponse,
                         handler: Any): Boolean = {
    request.getPathInfo
    log.trace(s"pre handle. request methd=${ request.getMethod }, query=${ request.getRequestURL }")

    super.preHandle(request, response, handler)
  }

  override def postHandle(request: HttpServletRequest,
                          response: HttpServletResponse,
                          handler: Any,
                          modelAndView: ModelAndView) {
    log.trace(s"post handle. response status=${ response.getStatus }, " +
              s"request methd=${ request.getMethod }, query=${ request.getRequestURL }")
    super.postHandle(request, response, handler, modelAndView)
  }
}
