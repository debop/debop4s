package debop4s.data.orm.jtests.mapping;

import debop4s.core.ToStringHelper;
import debop4s.core.ValueObjectBase;
import debop4s.core.utils.Hashs;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Embeddable;

@Embeddable
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
public class Address extends ValueObjectBase {

    private String street;
    private String city;
    private String state;
    private String country;

    private String zipcode;

    @Override
    public int hashCode() {
        return Hashs.compute(zipcode, street, city, state, country);
    }

    @Override
    public ToStringHelper buildStringHelper() {
        return super.buildStringHelper()
                    .add("zipcode", zipcode)
                    .add("country", country)
                    .add("state", state)
                    .add("city", city)
                    .add("street", street);
    }

    private static final long serialVersionUID = 8933074234998509782L;
}
