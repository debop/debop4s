package debop4s.data.orm.jtests.mapping.embeddable;

import debop4s.core.ToStringHelper;
import debop4s.data.orm.model.HibernateEntityBase;
import debop4s.data.orm.model.MinMaxNumber;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * MinMaxNumberEntity
 *
 * @author sunghyouk.bae@gmail.com
 */
@Entity
@Getter
@Setter
public class MinMaxNumberEntity extends HibernateEntityBase<Long> {

    @Id
    @GeneratedValue
    private Long id;

    @Embedded
    @AttributeOverrides({ @AttributeOverride(name = "min", column = @Column(name = "intMin")),
                          @AttributeOverride(name = "max", column = @Column(name = "intMax")), })
    private MinMaxNumber<Integer> intMinMax = new MinMaxNumber<>();

    @Embedded
    @AttributeOverrides({ @AttributeOverride(name = "min", column = @Column(name = "longMin")),
                          @AttributeOverride(name = "max", column = @Column(name = "longMax")), })
    private MinMaxNumber<Long> longMinMax = new MinMaxNumber<>();

    @Embedded
    @AttributeOverrides({ @AttributeOverride(name = "min", column = @Column(name = "floatMin")),
                          @AttributeOverride(name = "max", column = @Column(name = "floatMax")), })
    private MinMaxNumber<Float> floatMinMax = new MinMaxNumber<>();

    @Embedded
    @AttributeOverrides({ @AttributeOverride(name = "min", column = @Column(name = "doubleMin")),
                          @AttributeOverride(name = "max", column = @Column(name = "doubleMax")), })
    private MinMaxNumber<Double> doubleMinMax = new MinMaxNumber<>();


    @Override
    public ToStringHelper buildStringHelper() {
        return super.buildStringHelper()
                    .add("intMinMax", intMinMax)
                    .add("longMinMax", longMinMax)
                    .add("floatMinMax", floatMinMax)
                    .add("doubleMinMax", doubleMinMax);
    }

    private static final long serialVersionUID = 9085700187185088972L;

}
