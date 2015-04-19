package debop4s.data.orm.jtests.mapping.embeddable;

import debop4s.core.ToStringHelper;
import debop4s.core.utils.Hashs;
import debop4s.data.orm.model.HibernateEntityBase;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Index;

import javax.persistence.*;

@Entity
@org.hibernate.annotations.Cache(region = "data.embeddable", usage = CacheConcurrencyStrategy.READ_WRITE)
@org.hibernate.annotations.Table(appliesTo = "EmbeddableUser",
        indexes = {
                @Index(name = "ix_user_username",
                        columnNames = { "username", "password" })
        })
@DynamicInsert
@DynamicUpdate
@Getter
@Setter

public class EmbeddableUser extends HibernateEntityBase<Long> {

    @Id
    @GeneratedValue
    @Column(name = "UserId")
    @Setter(AccessLevel.PROTECTED)
    private Long id;

    private String firstname;

    private String lastname;

    @Column(length = 128)
    private String username;

    @Column(length = 64)
    private String password;

    @Column(name = "UserEmail")
    @Index(name = "ix_user_email")
    private String email;

    @Column(name = "IS_ACTIVE", nullable = false)
    private Boolean active = true;

    @Basic(fetch = FetchType.LAZY)
    private String exAttrs;

    @Embedded
    @AttributeOverrides(
            {
                    @AttributeOverride(name = "street", column = @Column(name = "HomeStreet", length = 128)),
                    @AttributeOverride(name = "zipcode", column = @Column(name = "HomeZipCode", length = 24)),
                    @AttributeOverride(name = "city", column = @Column(name = "HomeCity", length = 128)),
            }
    )
    private EmbeddableAddress homeAddress = new EmbeddableAddress();

    @Embedded
    @AttributeOverrides(
            {
                    @AttributeOverride(name = "street", column = @Column(name = "OfficeStreet", length = 128)),
                    @AttributeOverride(name = "zipcode", column = @Column(name = "OfficeZipCode", length = 24)),
                    @AttributeOverride(name = "city", column = @Column(name = "OfficeCity", length = 128)),
            }
    )
    private EmbeddableAddress officeAddress = new EmbeddableAddress();

    @Override
    public int hashCode() {
        return Hashs.compute(username, password);
    }

    @Override
    public ToStringHelper buildStringHelper() {
        return super.buildStringHelper()
                    .add("firstname", firstname)
                    .add("lastname", lastname)
                    .add("username", username)
                    .add("userpwd", password)
                    .add("userEmail", email)
                    .add("homeAddress", homeAddress)
                    .add("officeAddress", officeAddress);
    }

    private static final long serialVersionUID = -8207960118188526439L;
}
