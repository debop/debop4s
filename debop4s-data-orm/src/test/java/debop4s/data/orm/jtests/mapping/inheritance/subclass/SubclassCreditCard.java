package debop4s.data.orm.jtests.mapping.inheritance.subclass;

import debop4s.core.ToStringHelper;
import debop4s.core.utils.Hashs;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

/**
 * subclass 로 구현한 상속 클래스의 속성은 모두 nullable 이어야 한다. (한 테이블에 다른 클래스의 속성이 들어갈 때 현 클래스의 속성은 null로 설정되어야 한다.)
 * 속성 중에 not null 인 경우에는 SecondaryTable 을 이용한 join 을 구현하면 된다. (joined subclass 와는 다르다)
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 7. 2. 오후 9:53
 */
@Entity
@DiscriminatorValue(value = "CreditCard")
@SecondaryTable(name = "SubclassBilling_CreditCard",
        pkJoinColumns = @PrimaryKeyJoinColumn(name = "BillingId"))
@org.hibernate.annotations.Cache(region = "data.inheritance", usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
public class SubclassCreditCard extends SubclassBillingBase {

    // 이 것은 SubclassBillingBase 테이블에 저장된다.
    @Column(name = "CompanyName")
    private String cardCompanyname;

    // number, expMonth, expYear 는 not-null 이여야 하므로, 이 값들은 secondary table 에 저장하도록 한다.

    @Column(table = "SubclassBilling_CreditCard", name = "CardNumber", nullable = false)
    private String number;

    @Column(table = "SubclassBilling_CreditCard", name = "ExpireMonth", nullable = false)
    private Integer expMonth;

    @Column(table = "SubclassBilling_CreditCard", name = "ExpireYear", nullable = false)
    private Integer expYear;

    @Override
    public int hashCode() {
        return Hashs.compute(super.hashCode(), cardCompanyname, number, expMonth, expYear);
    }

    @Override
    public ToStringHelper buildStringHelper() {
        return super.buildStringHelper()
                    .add("number", number)
                    .add("expMonth", expMonth)
                    .add("expYear", expYear);
    }

    private static final long serialVersionUID = -2760188331805915479L;
}
