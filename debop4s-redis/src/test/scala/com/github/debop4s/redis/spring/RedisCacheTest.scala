package com.github.debop4s.redis.spring

import com.github.debop4s.redis.AbstractRedisTest
import com.github.debop4s.redis.model.User
import java.util.UUID
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.support.AnnotationConfigContextLoader
import org.springframework.test.context.{TestContextManager, ContextConfiguration}

/**
 * com.github.debop4s.redis.spring.RedisCacheTest 
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 24. 오전 11:00
 */
@RunWith(classOf[SpringJUnit4ClassRunner])
@ContextConfiguration(classes = Array(classOf[RedisCacheConfiguration]),
  loader = classOf[AnnotationConfigContextLoader])
class RedisCacheTest extends AbstractRedisTest {

  @Autowired private val cacheManager: RedisCacheManager = null
  @Autowired private val userRepository: UserRepository = null

  // Spring Autowired 를 수행합니다.
  new TestContextManager(this.getClass).prepareTestInstance(this)

  test("get cache") {
    cacheManager should not equal null
    val springCache = cacheManager.getCache("user")
    springCache should not equal null
  }

  test("spring cache get") {

    val user1 = userRepository.getUser("debop", 100)
    val user2 = userRepository.getUser("debop", 200)

    user1 should not equal null
    user1.favoriteMovies should not equal null
    user1.favoriteMovies.size should be > 0
    user1 should equal(user2)
    user1.favoriteMovies.size should equal(user2.favoriteMovies.size)
  }

  test("spring cache evict") {

    val userId = UUID.randomUUID().toString

    val user1: User = userRepository.getUser(userId, 100)
    val user2: User = userRepository.getUser(userId, 200)

    userRepository.updateUser(user1)

    val user3 = userRepository.getUser(userId, 200)

    user1 should equal(user2)
    user1.favoriteMovies.size should equal(user2.favoriteMovies.size)

    user3.favoriteMovies.size should not equal user1.favoriteMovies.size

  }

}
