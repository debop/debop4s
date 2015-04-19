package debop4s.data.orm.jtests.mapping.associations.join;

import debop4s.core.ToStringHelper;
import debop4s.core.ValueObjectBase;
import debop4s.core.utils.Hashs;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;

@Embeddable
@Getter
@Setter
public class JoinAddress extends ValueObjectBase {

    private String street;
    private String city;
    private String zipcode;

    @Override
    public int hashCode() {
        return Hashs.compute(street, city, zipcode);
    }

    @Override
    public ToStringHelper buildStringHelper() {
        return super.buildStringHelper()
                    .add("street", street)
                    .add("city", city)
                    .add("zipcode", zipcode);
    }

    private static final long serialVersionUID = -2928583180665391791L;
}
