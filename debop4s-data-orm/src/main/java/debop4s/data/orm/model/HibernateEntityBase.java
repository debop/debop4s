package debop4s.data.orm.model;

import debop4s.core.ToStringHelper;
import debop4s.core.utils.Hashs;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.MappedSuperclass;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;

/**
 * Hibernate, JPA 의 모든 엔티티의 기본 클래스입니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 6. 30. 오후 1:03
 */
@MappedSuperclass
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
abstract public class HibernateEntityBase<TId> extends PersistentObjectBase implements HibernateEntity<TId> {

    abstract public TId getId();

    // NOTE: JPA @PostPersist, @PostLoad 같은 경우는 @Entity 또는 @MappedSuperclass 여야만 작동한다.

    @Override
    @PostPersist
    public void onSave() {
        setPersisted(true);
    }

    @Override
    @PostLoad
    public void onLoad() {
        setPersisted(true);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object obj) {
        boolean isSameType = (obj != null) && getClass().equals(obj.getClass());

        if (isSameType) {
            HibernateEntity<TId> entity = (HibernateEntity<TId>) obj;
            return hasSameNonDefaultIdAs(entity) ||
                   ((!isPersisted() || !entity.isPersisted()) && hasSameBusinessSignature(entity));
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (getId() == null)
                ? System.identityHashCode(this)
                : Hashs.compute(getId());
    }

    @Override
    public ToStringHelper buildStringHelper() {
        return super.buildStringHelper()
                    .add("id", getId());
    }

    private boolean hasSameNonDefaultIdAs(HibernateEntity<TId> entity) {
        if (entity == null) return false;

        TId id = getId();
        TId entityId = entity.getId();
        return (id != null) && (entityId != null) && (id.equals(entityId));
    }

    private boolean hasSameBusinessSignature(HibernateEntity<TId> other) {
        boolean notNull = (other != null);
        int hash = (getId() != null) ? Hashs.compute(getId()) : hashCode();
        if (notNull) {
            int otherHash = (other.getId() != null) ? Hashs.compute(other.getId()) : other.hashCode();
            return hash == otherHash;
        }
        return false;
    }

    private static final long serialVersionUID = -4403625911423688674L;

}
