package debop4s.core.spring.annotations;

import org.springframework.context.annotation.Profile;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 제품 환경설정에 사용할 Annotation입니다. (Local -> Develop -> Test -> Production)
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @see @Test
 * @see @Production
 * @since 13. 9. 21. 오후 8:09
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Profile("PRODUCTION")
public @interface Production {
}
