package debop4s.data.orm.jtests.mapping.associations.onetomany.set;

import debop4s.core.utils.Hashs;
import debop4s.data.orm.model.HibernateEntityBase;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * one-to-many component 와 enum 값 설정에 대해
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 7. 3. 오전 9:27
 */
@Entity
@Getter
@Setter
public class ProductItem extends HibernateEntityBase<Long> {

    @Id
    @GeneratedValue
    @Column(name = "ProductItemId")
    @Setter(AccessLevel.PROTECTED)
    private Long id;

    private String name;
    private String description;
    private BigDecimal initialPrice;
    private BigDecimal reservePrice;
    @Temporal(TemporalType.DATE)
    private Date startDate;
    @Temporal(TemporalType.DATE)
    private Date endDate;

    // Enumerated 를 쓰면 Enum 값을 원하는 수형으로 DB에 저장하고, 반환받을 수 있다. 대부분 String 을 사용한다.
    @Enumerated(EnumType.STRING)
    @Column(name = "ItemStatus")
    private ProductStatus status;

    // one-to-many 에서 component (ValueObject) 로 매핑합니다. (class 매핑은 Bid 를 보세요)
    @CollectionTable(name = "ProductItemImage", joinColumns = @JoinColumn(name = "ProductItemId"))
    @ElementCollection(targetClass = ProductImage.class)
    @org.hibernate.annotations.Cascade(org.hibernate.annotations.CascadeType.ALL)
    @Fetch(FetchMode.SELECT)
    @BatchSize(size = 100)
    private Set<ProductImage> images = new HashSet<ProductImage>();

    public boolean removeImage(ProductImage image) {
        return images.remove(image);
    }


    @Override
    public int hashCode() {
        return Hashs.compute(name);
    }

    private static final long serialVersionUID = 5112039646445475518L;
}
