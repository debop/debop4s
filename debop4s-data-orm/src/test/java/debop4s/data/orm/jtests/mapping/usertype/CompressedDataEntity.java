package debop4s.data.orm.jtests.mapping.usertype;

import debop4s.core.ToStringHelper;
import debop4s.core.utils.Hashs;
import debop4s.data.orm.model.HibernateEntityBase;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * debop4s.data.orm.s.mapping.usertype.CompressedDataEntity
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 7. 3. 오전 11:34
 */
@Entity
@org.hibernate.annotations.Cache(region = "data.usertype", usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
public class CompressedDataEntity extends HibernateEntityBase<Long> {

    @Id
    @GeneratedValue
    @Setter(AccessLevel.PROTECTED)
    private Long id;

    @Column(name = "CompressedString")
    @Type(type = "debop4s.data.orm.hibernate.usertype.compress.GZipStringUserType")
    private String stringData;

    @Column(name = "CompressedBytes")
    @Type(type = "debop4s.data.orm.hibernate.usertype.compress.GZipBinaryUserType")
    private byte[] binaryData;

    @Override
    public int hashCode() {
        return Hashs.compute(stringData, binaryData);
    }

    @Override
    public ToStringHelper buildStringHelper() {
        return super.buildStringHelper()
                    .add("stringData", stringData);
    }

    private static final long serialVersionUID = -5969520267199034101L;
}
