package debop4s.data.orm.jtests.mapping.parallel;

import debop4s.core.utils.Hashs;
import debop4s.data.orm.model.HibernateEntityBase;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Cache(region = "parallel", usage = CacheConcurrencyStrategy.READ_WRITE)
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
public class ParallelOrder extends HibernateEntityBase<Long> {

    // 테스트 용이라 @GeneratedValue 는 뺐습니다.
    @Id
    private Long id;

    @Column(nullable = false, length = 128)
    private String no;

    @OneToMany(mappedBy = "order", cascade = { CascadeType.ALL }, orphanRemoval = true)
    private List<ParallelOrderItem> items = new ArrayList<ParallelOrderItem>();


    @Override
    public int hashCode() {
        return Hashs.compute(no);
    }

    private static final long serialVersionUID = -5269796360521679627L;
}
