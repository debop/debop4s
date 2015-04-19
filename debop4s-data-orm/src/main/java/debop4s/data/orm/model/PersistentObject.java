package debop4s.data.orm.model;

import debop4s.core.ValueObject;

/**
 * Hibernate, JPA 용 데이터를 표현하는 객체의 기본 클래스 (Entity나 Component의 조상입니다)
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 6. 30. 오후 12:30
 */
public interface PersistentObject extends ValueObject {

    /**
     * 엔티티가 영구 저장되었는지 여부
     *
     * @return true 면 영구 저장, false면 transient object 입니다.
     */
    boolean isPersisted();

    /** 엔티티 저장 시 호출되는 메소드 */
    void onSave();

    /** 엔티티 저장 시 호출되는 메소드 */
    void onPersist();

    /** 엔티티를 영구 저장소에서 로드 시에 호출되는 메소드 */
    void onLoad();
}
