package debop4s.rediscala.jtests.spring;

import debop4s.rediscala.model.User;
import debop4s.rediscala.model.User$;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

/**
 * UserRepository
 *
 * @author Sunghyouk Bae
 */
@Slf4j
@Repository
public class UserRepository {

    @Cacheable(value = { "user" }, key = "'user'.concat(':').concat(#id)")
    public User getUser(String id, int favoriteMovieSize) {
        log.info("새로운 사용자를 생성합니다. id=[{}]", id);
        User user = User$.MODULE$.apply(favoriteMovieSize);
        user.setId(id);
        return user;
    }

    @CacheEvict(value = { "user" }, key = "'user'.concat(':').concat(#user.id)")
    public void updateUser(User user) {
        log.debug("사용자의 정보를 갱신합니다... 캐시는 삭제됩니다...");
    }
}
