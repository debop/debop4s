package debop4s.data.orm.jtests.mapping.parallel;

import debop4s.data.orm.model.LongEntity;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Created by debop on 2014. 3. 12.
 */
@Entity
@Cache(region = "parallel", usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
public class ParallelOrderItem extends LongEntity {

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "orderId")
    private ParallelOrder order;

    private static final long serialVersionUID = -1007844932783263429L;
}
