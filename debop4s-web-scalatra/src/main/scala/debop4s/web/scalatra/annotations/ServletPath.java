package debop4s.web.scalatra.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Servlet의 경로를 표현하도록 합니다.
 * {{{
 *
 * @ServletPath("/user")
 * @Component class UserServlet extends ScalatraServlet {
 * //...
 * }
 * }}}
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ServletPath {
    String value();
}
