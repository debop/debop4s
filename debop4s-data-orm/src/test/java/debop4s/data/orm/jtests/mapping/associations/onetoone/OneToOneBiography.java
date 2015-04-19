package debop4s.data.orm.jtests.mapping.associations.onetoone;

import debop4s.core.ToStringHelper;
import debop4s.core.utils.Hashs;
import debop4s.data.orm.model.HibernateEntityBase;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

/**
 * debop4s.data.orm.s.mapping.associations.onetoone.OneToOneBiography
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 7. 2. 오후 9:59
 */
@Entity
@org.hibernate.annotations.Cache(region = "data.association", usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
public class OneToOneBiography extends HibernateEntityBase<Long> {

    protected OneToOneBiography() {
    }

    public OneToOneBiography(OneToOneAuthor author) {
        this.author = author;
    }

    @Id
    @Column(name = "authorId")
    @Setter(AccessLevel.PROTECTED)
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "authorId")
    private OneToOneAuthor author;

    private String information;

    @Override
    public int hashCode() {
        return Hashs.compute(information);
    }

    @Override
    public ToStringHelper buildStringHelper() {
        return super.buildStringHelper()
                    .add("author", author);
    }

    private static final long serialVersionUID = -2640595520119600607L;
}
