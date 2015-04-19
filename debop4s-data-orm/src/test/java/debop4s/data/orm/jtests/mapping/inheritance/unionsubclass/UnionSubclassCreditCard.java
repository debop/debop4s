package debop4s.data.orm.jtests.mapping.inheritance.unionsubclass;

import debop4s.core.ToStringHelper;
import debop4s.core.utils.Hashs;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * 상속된 클래스는 독립된 테이블을 가지므로 not null을 지정할 수 있다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 7. 2. 오후 9:54
 */
@Entity
@org.hibernate.annotations.Cache(region = "data.inheritance", usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
public class UnionSubclassCreditCard extends UnionSubclassBillingBase {

    @Column(name = "CompanyName")
    private String cardCompanyname;

    @Column(name = "CardNumber", nullable = false)
    private String number;

    @Column(name = "ExpireMonth", nullable = false)
    private Integer expMonth;

    @Column(name = "ExpireYear", nullable = false)
    private Integer expYear;

    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Temporal(TemporalType.DATE)
    private Date endDate;

    @Override
    public int hashCode() {
        return Hashs.compute(getOwner(), number);
    }

    @Override
    public ToStringHelper buildStringHelper() {
        return super.buildStringHelper()
                    .add("cardCompanyname", cardCompanyname)
                    .add("number", number)
                    .add("expMonth", expMonth)
                    .add("expYear", expYear);
    }

    private static final long serialVersionUID = 774812811646622523L;
}
