package debop4s.data.orm.jtests.mapping.compositeid.idclass;

import debop4s.core.ToStringHelper;
import debop4s.core.ValueObjectBase;
import debop4s.core.utils.Hashs;

/**
 * debop4s.data.orm.s.mapping.compositeid.idclass.CarIdentifier
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 7. 15. 오전 10:54
 */
public class CarIdentifier extends ValueObjectBase {

    private String brand;
    private int year;

    protected CarIdentifier() {
    }

    public CarIdentifier(String brand, int year) {
        this.brand = brand;
        this.year = year;
    }

    @Override
    public int hashCode() {
        return Hashs.compute(brand, year);
    }

    @Override
    public ToStringHelper buildStringHelper() {
        return super.buildStringHelper()
                    .add("brand", brand)
                    .add("year", year);
    }

    private static final long serialVersionUID = 6503259544107235251L;
}
