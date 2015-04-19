package debop4s.data.orm.jtests.mapping.associations.onetomany.set;

import debop4s.data.orm.jtests.jpa.JpaTestBase;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;

import static org.fest.assertions.Assertions.assertThat;

/**
 * debop4s.data.orm.s.mapping.associations.onetomany.set.SetTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 7. 22. 오후 2:48
 */
@Slf4j
@Transactional
public class SetTest extends JpaTestBase {

    @PersistenceContext
    EntityManager em;

    @Test
    public void bidding() {
        BiddingItem item = new BiddingItem();
        Bid bid1 = new Bid(item, new BigDecimal(100.0));
        Bid bid2 = new Bid(item, new BigDecimal(200.0));

        em.persist(item);
        em.flush();
        em.clear(); // test 시에만 호출

        item = em.find(BiddingItem.class, item.getId());
        assertThat(item).isNotNull();
        assertThat(item.getBids().size()).isEqualTo(2);

        bid1 = item.getBids().iterator().next();
        item.getBids().remove(bid1);

        em.persist(item);
        em.flush();
        em.clear();

        item = em.find(BiddingItem.class, item.getId());
        assertThat(item).isNotNull();
        assertThat(item.getBids()).hasSize(1);

        em.remove(item);
        em.flush();
        assertThat(em.find(BiddingItem.class, item.getId())).isNull();
    }

    @Test
    public void productTest() {

        ProductItem item = new ProductItem();

        ProductImage image1 = new ProductImage();
        item.getImages().add(image1);
        image1.setItem(item);
        image1.setName("image1");

        ProductImage image2 = new ProductImage();
        item.getImages().add(image2);
        image2.setItem(item);
        image2.setName("image2");

        item.setStatus(ProductStatus.Active);

        em.persist(item);
        em.flush();
        em.clear();  // test 시에만 호출

        ProductItem loaded = em.find(ProductItem.class, item.getId());
        assertThat(loaded.getImages().size()).isEqualTo(2);

        loaded.getImages().clear();

        em.persist(loaded);
        em.flush();
        em.clear();  // test 시에만 호출

        loaded = em.find(ProductItem.class, item.getId());
        assertThat(loaded).isNotNull();
        assertThat(loaded.getImages().size()).isEqualTo(0);

        em.remove(loaded);
        em.flush();
        assertThat(em.find(ProductItem.class, item.getId())).isNull();
    }
}
