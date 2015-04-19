package debop4s.data.orm.jtests.mapping.parallel;

import debop4s.core.collections.NumberRange;
import debop4s.data.orm.jpa.utils.JpaParCallable;
import debop4s.data.orm.jpa.utils.JpaParallels;
import debop4s.data.orm.jtests.jpa.JpaTestBase;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.modelmapper.internal.util.Lists;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;

@Slf4j
@Transactional
@Ignore("현재 테스트 중입니다. 멀티스레드로 JPA 작업은 하지 마시기 바랍니다.")
public class JpaParallelsTest extends JpaTestBase {

    @PersistenceContext EntityManager em;
    private static final long entityCount = 10;

    @Before
    public void before() {
        em.createQuery("delete from ParallelOrderItem").executeUpdate();
        em.createQuery("delete from ParallelOrder").executeUpdate();
        em.flush();
        em.clear();
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Rollback(false)
    public void parallelSaveAndRead() {
        final List<Long> orderIds = new ArrayList<Long>();

        final List<Long> orderNos = Lists.from(NumberRange.range(entityCount).iterator());

        for (long i = 0; i < entityCount; i++) {
            ParallelOrder order = createOrder(i);
            order.setId(i);
            em.persist(order);
            orderIds.add(i);
        }
        em.flush();
        em.clear();

        log.debug("orderIds=[{}]", orderIds);

        Iterable<ParallelOrder> orders =
                JpaParallels.callFunc(emf, orderIds, new JpaParCallable<Long, ParallelOrder>() {
                    @Override
                    public ParallelOrder call(EntityManager em, Long elem) {
                        log.debug("load order. id=[{}]", elem);
                        ParallelOrder order = em.find(ParallelOrder.class, elem);
                        Hibernate.initialize(order.getItems());
                        em.detach(order);
                        return order;
                    }
                });
        for (ParallelOrder order : orders) {
            assertThat(order).isNotNull();
        }

        // read from second cache
        orders = JpaParallels.callFunc(emf, orderIds, new JpaParCallable<Long, ParallelOrder>() {
            @Override
            public ParallelOrder call(EntityManager em, Long elem) {
                ParallelOrder order = em.find(ParallelOrder.class, elem);
                Hibernate.initialize(order.getItems());
                return order;
            }
        });
        for (ParallelOrder order : orders) {
            assertThat(order).isNotNull();
        }

        orders = JpaParallels.callFunc(emf, orderIds, new JpaParCallable<Long, ParallelOrder>() {
            @Override
            public ParallelOrder call(EntityManager em, Long elem) {
                ParallelOrder order = em.find(ParallelOrder.class, elem);
                Hibernate.initialize(order.getItems());
                return order;
            }
        });
        for (ParallelOrder order : orders) {
            assertThat(order).isNotNull();
        }

    }

    private ParallelOrder createOrder(Long orderNo) {
        ParallelOrder order = new ParallelOrder();
        order.setNo("order no." + orderNo);

        ParallelOrderItem item1 = new ParallelOrderItem();
        item1.setName("Item1-" + orderNo);
        item1.setOrder(order);
        order.getItems().add(item1);

        ParallelOrderItem item2 = new ParallelOrderItem();
        item2.setName("Item1-" + orderNo);
        item2.setOrder(order);
        order.getItems().add(item2);

        return order;
    }
}
