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
 * debop4s.data.orm.s.mapping.associations.onetoone.OneToOneAuthorPicture
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 14. 오전 9:15
 */
@Entity
@org.hibernate.annotations.Cache(region = "data.association", usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicInsert
@DynamicUpdate
@Getter
public class OneToOneAuthorPicture extends HibernateEntityBase<Long> {

    protected OneToOneAuthorPicture() {
    }

    public OneToOneAuthorPicture(OneToOneAuthor author) {
        this.author = author;
    }

    @Id
    @Column(name = "authorId")
    @Setter(AccessLevel.PROTECTED)
    private Long id;

    @MapsId
    @OneToOne(cascade = { CascadeType.REMOVE }, fetch = FetchType.LAZY)
    @JoinColumn(name = "authorId")
    private OneToOneAuthor author;

    @Setter
    private String picturePath;

    @Override
    public int hashCode() {
        return Hashs.compute(picturePath);
    }

    @Override
    public ToStringHelper buildStringHelper() {
        return super.buildStringHelper()
                    .add("picturePath", picturePath);
    }

    private static final long serialVersionUID = 7251035949693027858L;
}
