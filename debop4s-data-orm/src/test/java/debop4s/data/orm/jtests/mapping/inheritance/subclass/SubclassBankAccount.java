package debop4s.data.orm.jtests.mapping.inheritance.subclass;

import debop4s.core.ToStringHelper;
import debop4s.core.utils.Hashs;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * subclass 로 구현한 상속 클래스의 속성은 모두 nullable 이어야 한다. (한 테이블에 다른 클래스의 속성이 들어갈 때 현 클래스의 속성은 null로 설정되어야 한다.)
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 7. 2. 오후 9:52
 */
@Entity
@DiscriminatorValue("BankAccount")
@org.hibernate.annotations.Cache(region = "data.inheritance", usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
public class SubclassBankAccount extends SubclassBillingBase {

    @Column(name = "BackAccount")
    private String account;

    @Column(name = "BankName")
    private String bankname;

    @Column(name = "BankSwift")
    private String swift;

    @Override
    public int hashCode() {
        return Hashs.compute(super.hashCode(), account, bankname);
    }

    @Override
    public ToStringHelper buildStringHelper() {
        return super.buildStringHelper()
                    .add("account", account)
                    .add("bankname", bankname)
                    .add("swift", swift);
    }

    private static final long serialVersionUID = 8591422969532083371L;
}
