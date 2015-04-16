package debop4s.core.spring.annotations;

import org.springframework.context.annotation.Profile;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 로컬 개발 상태의 환경설정에 사용할 Annotation입니다. (Local -> Develop -> Test -> Production)
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 9. 23. 오후 1:15
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Profile("LOCAL")
public @interface Local {
}
