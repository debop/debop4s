package debop4s.rediscala.jtests.spring;

import debop4s.rediscala.model.User;
import debop4s.rediscala.spring.RedisCacheManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.UUID;

import static org.fest.assertions.Assertions.assertThat;

/**
 * RedisCacheTest
 *
 * @author Sunghyouk Bae
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { RedisCacheConfiguration.class })
public class RedisCacheTest {

    @Autowired RedisCacheManager cacheManager;
    @Autowired UserRepository userRepository;

    @Test
    public void testGetCache() throws Exception {
        assertThat(cacheManager).isNotNull();
        Cache cache = cacheManager.getCache("user");
        assertThat(cache).isNotNull();
    }

    @Test
    public void testSpringCacheGet() throws Exception {
        User user1 = userRepository.getUser("debop", 100);
        User user2 = userRepository.getUser("debop", 200);

        assertThat(user1).isNotNull();
        assertThat(user1.favoriteMovies()).isNotNull();
        assertThat(user1.favoriteMovies().size()).isGreaterThan(0);
        assertThat(user1).isEqualTo(user2);
        assertThat(user1.favoriteMovies().size()).isEqualTo(user2.favoriteMovies().size());
    }

    @Test
    public void testSpringCacheEvict() throws Exception {
        String userId = UUID.randomUUID().toString();

        User user1 = userRepository.getUser(userId, 100);
        User user2 = userRepository.getUser(userId, 200);

        userRepository.updateUser(user1);

        User user3 = userRepository.getUser(userId, 200);

        assertThat(user1).isEqualTo(user2);
        assertThat(user1.favoriteMovies().size()).isEqualTo(user2.favoriteMovies().size());
        assertThat(user3.favoriteMovies().size()).isNotEqualTo(user1.favoriteMovies().size());

    }
}
