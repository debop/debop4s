package debop4s.data.orm.jtests.mapping.associations.onetomany.list;

import debop4s.data.orm.jtests.jpa.JpaTestBase;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.fest.assertions.Assertions.assertThat;


@Slf4j
@Transactional
public class OneToManyListTest extends JpaTestBase {

    @PersistenceContext
    EntityManager em;

    @Test
    public void simpleOneToMany() {
        OneToManyOrder order = new OneToManyOrder();

        OneToManyOrderItem item1 = new OneToManyOrderItem();
        item1.setName("Item1");
        item1.setOrder(order);
        order.getItems().add(item1);

        OneToManyOrderItem item2 = new OneToManyOrderItem();
        item2.setName("Item1");
        item2.setOrder(order);
        order.getItems().add(item2);

        em.persist(order);
        em.flush();
        em.clear();

        order = em.find(OneToManyOrder.class, order.getId());
        assertThat(order.getItems()).hasSize(2);

        OneToManyOrderItem item = order.getItems().iterator().next();
        order.getItems().remove(item);
        em.persist(order);
        em.flush();
        em.clear();

        order = em.find(OneToManyOrder.class, order.getId());
        assertThat(order).isNotNull();
        assertThat(order.getItems()).hasSize(1);

        em.remove(order);
        em.flush();

        assertThat(em.find(OneToManyOrder.class, order.getId())).isNull();
    }

    @Test
    public void orderedListTest() throws Exception {

        OneToManyChild luke = new OneToManyChild("luke");
        OneToManyChild leia = new OneToManyChild("leia");
        em.persist(luke);
        em.persist(leia);

        OneToManyFather father = new OneToManyFather();
        father.getOrderedChildren().add(luke);
        father.getOrderedChildren().add(null);
        father.getOrderedChildren().add(leia);

        em.persist(father);
        em.flush();
        em.clear();

        father = em.find(OneToManyFather.class, father.getId());

        assertThat(father.getOrderedChildren())
                .as("List should have 3 elements")
                .hasSize(3);

        assertThat(father.getOrderedChildren().get(0).getName())
                .as("Luke should be first")
                .isEqualTo(luke.getName());

        assertThat(father.getOrderedChildren().get(1))
                .as("Second born should be null")
                .isNull();

        assertThat(father.getOrderedChildren().get(2).getName())
                .as("Leia should be third")
                .isEqualTo(leia.getName());


        em.remove(father);
        em.flush();

        assertThat(em.find(OneToManyFather.class, father.getId())).isNull();
    }

    @Test
    public void mapAndElementCollection() throws Exception {

        OneToManyAddress home = new OneToManyAddress();
        home.setCity("Paris");

        OneToManyAddress work = new OneToManyAddress();
        work.setCity("San Francisco");

        OneToManyUser user = new OneToManyUser();
        user.getAddresses().put("home", home);
        user.getAddresses().put("work", work);
        user.getNicknames().add("idrA");
        user.getNicknames().add("day[9]");

        em.persist(home);
        em.persist(work);
        em.persist(user);

        OneToManyUser user2 = new OneToManyUser();
        user2.getNicknames().add("idrA");
        user2.getNicknames().add("day[9]");

        em.persist(user2);
        em.flush();
        em.clear();

        user = em.find(OneToManyUser.class, user.getId());

        assertThat(user.getNicknames()).as("Should have 2 nick1").hasSize(2);
        assertThat(user.getNicknames()).as("Should contain nicks").contains("idrA", "day[9]");

        user.getNicknames().remove("idrA");
        user.getAddresses().remove("work");

        em.persist(user);
        em.flush();
        em.clear();

        user = em.find(OneToManyUser.class, user.getId());

        // TODO do null value
        assertThat(user.getAddresses()).as("List should have 1 elements").hasSize(1);
        assertThat(user.getAddresses().get("home").getCity()).as("home address should be under home").isEqualTo(home.getCity());
        assertThat(user.getNicknames()).as("Should have 1 nick1").hasSize(1);
        assertThat(user.getNicknames()).as("Should contain nick").contains("day[9]");

        em.remove(user);

        // CascadeType.ALL 로 user 삭제 시 address 삭제 됨
        // em.srem(em.load(Address.class, home.getId()));
        // em.srem(em.load(Address.class, work.getId()));

        user2 = em.find(OneToManyUser.class, user2.getId());
        assertThat(user2.getNicknames()).as("Should have 2 nicks").hasSize(2);
        assertThat(user2.getNicknames()).as("Should contain nick").contains("idrA", "day[9]");
        em.remove(user2);
        em.flush();


        assertThat(em.find(OneToManyUser.class, user.getId())).isNull();
        assertThat(em.find(OneToManyUser.class, user2.getId())).isNull();
    }


}
