package debop4s.data.orm.jtests.mapping.associations.join;

import debop4s.data.orm.jtests.jpa.JpaTestBase;
import debop4s.data.orm.jtests.mapping.associations.join.repository.JoinCustomerRepository;
import debop4s.data.orm.jtests.mapping.associations.join.repository.JoinUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.fest.assertions.Assertions.assertThat;


@Slf4j
@Transactional
public class JoinTest extends JpaTestBase {

    @PersistenceContext
    EntityManager em;

    @Autowired JoinUserRepository userRepository;

    @Autowired JoinCustomerRepository customerRepository;

    @Test
    public void configurationTest() {
        assertThat(userRepository).isNotNull();
    }

    @Test
    public void joinUserTest() {
        JoinUser user = new JoinUser();
        user.getNicknames().add("debop");
        user.getNicknames().add("sunghyouk");

        JoinAddressEntity home = new JoinAddressEntity();
        home.setCity("Seoul");
        home.setStreet("Jungreung");
        home.setZipcode("100-100");
        user.getAddresses().put("home", home);

        JoinAddressEntity office = new JoinAddressEntity();
        office.setCity("Seoul");
        office.setStreet("Ankook");
        office.setZipcode("200-200");
        user.getAddresses().put("office", office);

        userRepository.save(user);
        em.flush();
        em.clear();

        JoinUser loaded = userRepository.findOne(user.getId());

        assertThat(loaded).isNotNull();
        assertThat(loaded.getAddresses()).isNotNull();
        assertThat(loaded.getAddresses().size()).isEqualTo(2);
        assertThat(loaded.getNicknames().size()).isEqualTo(2);

        userRepository.delete(loaded);
        userRepository.flush();

        loaded = userRepository.findOne(user.getId());
        assertThat(loaded).isNull();
    }

    @Test
    public void joinCustomerTest() {

        JoinCustomer customer = new JoinCustomer();
        customer.setName("배성혁");
        customer.setEmail("sunghyouk.bae@gmail.com");

        JoinAddress addr = new JoinAddress();
        addr.setCity("Seoul");
        addr.setStreet("Jungreung");
        addr.setZipcode("100-100");

        // Embedded Class
        customer.setJoinAddress(addr);
        customerRepository.save(customer);
        em.flush();
        em.clear();

        JoinCustomer loaded = customerRepository.findByName(customer.getName());

        assertThat(loaded).isNotNull();
        assertThat(loaded.getJoinAddress()).isNotNull();
        assertThat(loaded.getJoinAddress().getCity()).isEqualToIgnoringCase(addr.getCity());

        customerRepository.delete(loaded);

        loaded = customerRepository.findByName(customer.getName());
        assertThat(loaded).isNull();
    }

}
