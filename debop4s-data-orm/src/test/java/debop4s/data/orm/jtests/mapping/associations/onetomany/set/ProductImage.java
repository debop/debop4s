package debop4s.data.orm.jtests.mapping.associations.onetomany.set;

import debop4s.core.ToStringHelper;
import debop4s.core.ValueObjectBase;
import debop4s.core.utils.Hashs;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Parent;

import javax.persistence.Embeddable;

/**
 * debop4s.data.orm.s.mapping.associations.onetomany.set.ProductImage
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 7. 3. 오전 9:27
 */
@Embeddable
@Getter
@Setter
public class ProductImage extends ValueObjectBase {


    // Component 가 소유자 Entity를 양방향으로 연관지을 때 사용합니다.
    @Parent
    private ProductItem item;

    private String name;

    private String filename;

    private Integer sizeX;

    private Integer sizeY;

    @Override
    public int hashCode() {
        return Hashs.compute(name, filename);
    }

    @Override
    public ToStringHelper buildStringHelper() {
        return super.buildStringHelper()
                    .add("name", name)
                    .add("filename", filename);
    }

    private static final long serialVersionUID = 8180808927020179158L;
}
