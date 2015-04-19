package debop4s.data.orm.jtests.mapping.associations.join;

import debop4s.core.ToStringHelper;
import debop4s.core.utils.Hashs;
import debop4s.data.orm.model.HibernateEntityBase;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


@Entity
@org.hibernate.annotations.Cache(region = "data.association", usage = CacheConcurrencyStrategy.READ_WRITE)
@Getter
@Setter
public class JoinUser extends HibernateEntityBase<Long> {

    @Id
    @GeneratedValue
    @Setter(AccessLevel.PROTECTED)
    private Long id;

    private String name;

    // @OneToMany를 이용한 Mapping 은 Entity여야 합니다.
    // 1:N 테이블 매핑을 수행합니다.
    @OneToMany(cascade = { CascadeType.ALL }, orphanRemoval = true)
    @JoinTable(name = "JoinUserAddressMap")
    @MapKeyColumn(name = "nick")
    @ElementCollection(targetClass = JoinAddressEntity.class, fetch = FetchType.EAGER)
    @Fetch(FetchMode.SUBSELECT)
    private Map<String, JoinAddressEntity> addresses = new HashMap<String, JoinAddressEntity>();

    // 1:N 테이블 매핑을 수행합니다. (단순 수형인 경우 간단하게 처리됩니다)
    @JoinTable(name = "JoinUserNicknameMap", joinColumns = { @JoinColumn(name = "UserId") })
    @ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
    @Cascade(value = { org.hibernate.annotations.CascadeType.ALL })
    private Set<String> nicknames = new HashSet<String>();

    @Override
    public int hashCode() {
        return Hashs.compute(name);
    }

    @Override
    public ToStringHelper buildStringHelper() {
        return super.buildStringHelper()
                    .add("name", name);
    }

    private static final long serialVersionUID = 6576034453765250546L;
}
