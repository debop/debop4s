package debop4s.data.orm.model;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Meta 정보를 가진 엔티티
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 6. 30. 오후 2:04
 */
abstract public class MetaEntityBase<TId> extends HibernateEntityBase<TId> implements MetaEntity {

    private Map<String, MetaValue> metaMap = new LinkedHashMap<String, MetaValue>();

    @Override
    public MetaValue getMetaValue(final String key) {
        return metaMap.get(key);
    }

    @Override
    public Set<String> getMataKeys() {
        return metaMap.keySet();
    }

    @Override
    public void addMeta(final String key, final MetaValue metaValue) {
        metaMap.put(key, metaValue);
    }

    @Override
    public void addMeta(final String key, final Object value) {
        metaMap.put(key, new MetaValueImpl(value));
    }

    @Override
    public void removeMeta(final String key) {
        metaMap.remove(key);
    }

    private static final long serialVersionUID = 2290859959720539740L;
}
