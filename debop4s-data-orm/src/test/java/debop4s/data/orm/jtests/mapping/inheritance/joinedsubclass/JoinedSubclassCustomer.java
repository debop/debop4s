package debop4s.data.orm.jtests.mapping.inheritance.joinedsubclass;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * JoinedSubclassCustomer
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 7. 3. 오전 11:18
 */
@Entity
@org.hibernate.annotations.Cache(region = "data.inheritance", usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
public class JoinedSubclassCustomer extends JoinedSubclassPerson {

    @ManyToOne
    @JoinColumn(name = "ContactEmployeeId", nullable = false)
    private JoinedSubclassEmployee contactEmployee;

    private static final long serialVersionUID = 1966556953690567037L;
}
