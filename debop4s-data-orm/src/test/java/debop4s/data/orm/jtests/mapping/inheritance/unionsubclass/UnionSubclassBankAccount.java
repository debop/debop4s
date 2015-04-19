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
public class UnionSubclassBankAccount extends UnionSubclassBillingBase {

    @Column(name = "BackAccount", nullable = false)
    private String account;

    @Column(name = "BankName", nullable = false)
    private String bankname;

    @Column(name = "BankSwift")
    private String swift;

    @Override
    public int hashCode() {
        return Hashs.compute(getOwner(), account);
    }

    @Override
    public ToStringHelper buildStringHelper() {
        return super.buildStringHelper()
                    .add("account", account)
                    .add("bankname", bankname)
                    .add("swift", swift);
    }

    private static final long serialVersionUID = 3847385258119902407L;
}
