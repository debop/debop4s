package debop4s.data.orm.jtests.mapping.embeddable;

import debop4s.core.ToStringHelper;
import debop4s.core.ValueObjectBase;
import debop4s.core.utils.Hashs;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Embeddable;

@Embeddable
@org.hibernate.annotations.Cache(region = "data.embeddable", usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
public class EmbeddableAddress extends ValueObjectBase {

    private String street;
    private String zipcode;
    private String city;

    @Override
    public int hashCode() {
        return Hashs.compute(zipcode, street, city);
    }

    @Override
    public ToStringHelper buildStringHelper() {
        return super.buildStringHelper()
                    .add("zipcode", zipcode)
                    .add("street", street)
                    .add("city", city);
    }

    private static final long serialVersionUID = 1543991184434499594L;
}
