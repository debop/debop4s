package debop4s.data.orm.jtests.mapping.associations.onetomany.set;

import debop4s.core.ToStringHelper;
import debop4s.core.utils.Hashs;
import debop4s.data.orm.model.HibernateEntityBase;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;


@Entity
@Getter
@Setter
public class BiddingItem extends HibernateEntityBase<Long> {

    @Id
    @GeneratedValue
    @Column(name = "ITEM_ID")
    @Setter(AccessLevel.PROTECTED)
    private Long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "DESCRIPTION")
    private String description;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    @Fetch(FetchMode.SELECT)
    @BatchSize(size = 100)
    private java.util.Set<Bid> bids = new java.util.HashSet<Bid>();

    @Override
    public int hashCode() {
        return Hashs.compute(name);
    }

    @Override
    public ToStringHelper buildStringHelper() {
        return super.buildStringHelper()
                    .add("name", name)
                    .add("description", description);
    }

    private static final long serialVersionUID = -3034221238074886669L;
}
