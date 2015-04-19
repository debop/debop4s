package debop4s.data.orm.jtests.mapping.associations.onetomany.list;

import debop4s.core.ToStringHelper;
import debop4s.core.utils.Hashs;
import debop4s.data.orm.model.HibernateEntityBase;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.*;

import javax.persistence.CascadeType;
import javax.persistence.*;
import javax.persistence.Entity;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


@Entity
@org.hibernate.annotations.Cache(region = "data.association", usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
public class OneToManyUser extends HibernateEntityBase<Long> {

    @Id
    @GeneratedValue
    @Column(name = "UserId")
    @Setter(AccessLevel.PROTECTED)
    private Long id;

    private String city;

    @OneToMany(cascade = { CascadeType.ALL })
    @JoinTable(name = "OneToMany_User_Address")
    @MapKeyColumn(name = "nick")
    @Fetch(FetchMode.SUBSELECT)
    private Map<String, OneToManyAddress> addresses = new HashMap<String, OneToManyAddress>();

    @ElementCollection
    @JoinTable(name = "OneToMany_Nicks", joinColumns = { @JoinColumn(name = "userId") })
    @Cascade({ org.hibernate.annotations.CascadeType.ALL })
    Set<String> nicknames = new HashSet<String>();

    @Override
    public int hashCode() {
        return Hashs.compute(city);
    }

    @Override
    public ToStringHelper buildStringHelper() {
        return super.buildStringHelper()
                    .add("city", city);
    }

    private static final long serialVersionUID = 4665117112272917082L;
}
