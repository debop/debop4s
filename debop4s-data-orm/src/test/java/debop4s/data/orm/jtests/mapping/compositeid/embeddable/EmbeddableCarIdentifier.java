package debop4s.data.orm.jtests.mapping.compositeid.embeddable;

import debop4s.core.ToStringHelper;
import debop4s.core.ValueObjectBase;
import debop4s.core.utils.Hashs;
import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * debop4s.data.orm.s.mapping.compositeid.embeddable.EmbeddableCarIdentifier
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 7. 15. 오전 10:48
 */
@Embeddable
@Getter
public class EmbeddableCarIdentifier extends ValueObjectBase {

    @Column(name = "brand", nullable = false, length = 32)
    private String brand;

    @Column(name = "releaseYear", nullable = false)
    private int year;

    protected EmbeddableCarIdentifier() {
    }

    public EmbeddableCarIdentifier(String brand, int year) {
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

    private static final long serialVersionUID = 1242677041869676373L;
}
