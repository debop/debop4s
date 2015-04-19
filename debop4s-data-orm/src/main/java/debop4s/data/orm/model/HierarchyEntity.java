package debop4s.data.orm.model;

import java.io.Serializable;
import java.util.Set;

/**
 * 계층형 자료 구조를 표현하며, 조상과 자손에 대한 컬렉션 속성을 가지는 인터페이스입니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 6. 30. 오후 1:21
 */
public interface HierarchyEntity<T extends HierarchyEntity<T>> {

    /** 계층형 구조 엔티티의 Identifier */
    Serializable getId();

    /** 조상 엔티티의 컬렉션 */
    Set<T> getAncestors();

    /** 자손 엔티티의 컬렉션 */
    Set<T> getDescendents();

}
