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
 * debop4s.data.orm.s.mapping.associations.onetoone.OneToOneAuthor
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
public class OneToOneAuthor extends HibernateEntityBase<Long> {

    @Id
    @GeneratedValue
    @Column(name = "authorId")
    @Setter(AccessLevel.PROTECTED)
    private Long id;

    private String name;

    @OneToOne(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    private OneToOneBiography biography;  // new OneToOneBiography(this);

    public OneToOneBiography getBiography() {
        if (biography == null)
            biography = new OneToOneBiography(this);
        return biography;
    }

    @OneToOne(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn
    private OneToOneAuthorPicture picture;

    public OneToOneAuthorPicture getPicture() {
        if (picture == null)
            picture = new OneToOneAuthorPicture(this);
        return picture;
    }

    @Override
    public int hashCode() {
        return Hashs.compute(name);
    }

    @Override
    public ToStringHelper buildStringHelper() {
        return super.buildStringHelper()
                    .add("name", name);
    }

    private static final long serialVersionUID = 3214527152785992305L;
}
