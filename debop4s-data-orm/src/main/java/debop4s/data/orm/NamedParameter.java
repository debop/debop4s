package debop4s.data.orm;

import java.io.Serializable;

/**
 * 쿼리 등에 사용할 Parameter의 기본 interface
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 6. 30. 오후 2:41
 */
public interface NamedParameter extends Serializable {

    /** 인자 명 */
    String getName();

    /** 인자 값 */
    Object getValue();

    /**
     * 파라미터 값 설정
     *
     * @param value 새로 설정할 파라미터 값
     */
    void setValue(Object value);
}
