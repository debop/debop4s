package debop4s.data.orm.jtests.mapping.associations.onetomany.map;

import debop4s.core.ValueObjectBase;
import debop4s.core.utils.Hashs;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;


@Embeddable
@Getter
@Setter
public class CarOption extends ValueObjectBase {

    public CarOption() {
    }

    public CarOption(String name, Integer value) {
        this.name = name;
        this.value = value;
    }

    @Column(name = "OptionName")
    private String name;

    @Column(name = "OptionValue")
    private Integer value;

    @Override
    public int hashCode() {
        return Hashs.compute(name);
    }

    private static final long serialVersionUID = 3280681050824736113L;
}
