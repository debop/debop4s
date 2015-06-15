package debop4s.mongo.spring

import java.util.UUID

import debop4s.core._
import debop4s.core.utils.ClosableStopwatch
import debop4s.mongo.AbstractMongoFunSuite
import debop4s.mongo.spring.model.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.support.AnnotationConfigContextLoader
import org.springframework.test.context.{ContextConfiguration, TestContextManager}

/**
 * MongoCacheFunSuite
 * @author sunghyouk.bae@gmail.com 14. 10. 18.
 */
@ContextConfiguration(classes = Array(classOf[MongoCacheConfiguration]),
  loader = classOf[AnnotationConfigContextLoader])
class MongoCacheFunSuite extends AbstractMongoFunSuite {

  @Autowired val cacheManager: MongoCacheManager = null
  @Autowired val userRepo: UserRepository = null

  override def beforeAll() {
    // NOTE: TestContextManager#prepareTestInstance 를 실행시켜야 제대로 Dependency Injection이 됩니다.
    new TestContextManager(this.getClass).prepareTestInstance(this)
  }

  test("configuration test") {
    cacheManager should not be null
    val springCache = cacheManager.getCache("user")
    springCache should not be null
  }

  test("user for cache") {
    var user1: User = null
    var user2: User = null

    using(new ClosableStopwatch("initial user")) { stopwatch =>
      user1 = userRepo.getUser("debop", 100)
    }
    using(new ClosableStopwatch("from cache")) { stopwatch =>
      user2 = userRepo.getUser("debop", 200)
    }

    user1 shouldEqual user2
    user1.favoriteMovies.size shouldEqual user2.favoriteMovies.size
  }

  test("cache evict") {
    val userId: String = UUID.randomUUID.toString

    var user1: User = null
    var user2: User = null
    var user3: User = null


    using(new ClosableStopwatch("initial user")) { stopwatch =>
      user1 = userRepo.getUser(userId, 100)
    }

    using(new ClosableStopwatch("from cache")) { stopwatch =>
      user2 = userRepo.getUser(userId, 200)
    }

    // evict user1
    userRepo.updateUser(user1)

    using(new ClosableStopwatch("after evict")) { stopwatch =>
      user3 = userRepo.getUser(userId, 200)
    }

    user1 shouldEqual user2
    user1.favoriteMovies.size shouldEqual user2.favoriteMovies.size
    user1.favoriteMovies.size should not equal user3.favoriteMovies.size
  }
}
