package debop4s.data.orm.model;

import java.util.Set;

/**
 * 메타 정보를 가지는 엔티티
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 6. 30. 오후 1:59
 */
public interface MetaEntity extends PersistentObject {

    /**
     * 메타 키에 해당하는 메타 값을 반환합니다.
     *
     * @param key 메타 키
     * @return 메타 값
     */
    MetaValue getMetaValue(final String key);


    /**
     * 엔티티가 가진 메타 키 컬렉션을 반환합니다.
     *
     * @return 메타 키의 컬렉션
     */
    Set<String> getMataKeys();

    /**
     * 메타 정보를 추가합니다.
     *
     * @param key       메타 키
     * @param metaValue 메타 값
     */
    void addMeta(final String key, final MetaValue metaValue);

    /**
     * 메타 정보를 추가합니다.
     *
     * @param key   메타 키
     * @param value 메타 값
     */
    void addMeta(final String key, Object value);

    /**
     * 해당 메타 정보를 삭제합니다.
     *
     * @param key 메타 키
     */
    void removeMeta(final String key);

}
