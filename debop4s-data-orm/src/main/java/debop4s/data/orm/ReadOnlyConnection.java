package debop4s.data.orm;

import java.lang.annotation.*;

/**
 * DB 작업 시 읽기전용으로 작업하라고 지시하는 Annotation 입니다.
 * <p/>
 * 메소드에 @ReadOnlyConnection 이 정의되어 있으면 AOP를 통해
 * {@link JpaReadOnlyInterceptor} 가 Connection 의 isReadOnly 속성을 true 하여, 작업하고,
 * 작업이 완료되면, 원래 값으로 복원시켜 줍니다.
 *
 * @author sunghyouk.bae@gmail.com
 * @deprecated 그냥 @Transactional(readOnly=true) 를 사용하세요.
 */
@Deprecated
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ReadOnlyConnection {
}

