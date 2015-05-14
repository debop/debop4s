package debop4s.rediscala.spring

import java.util.UUID

import org.scalatest._
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.support.AnnotationConfigContextLoader
import org.springframework.test.context.{ ContextConfiguration, TestContextManager }

@ContextConfiguration(classes = Array(classOf[RedisCacheConfiguration]), loader = classOf[AnnotationConfigContextLoader])
class RedisCacheFunSuite
  extends FunSuite with Matchers with OptionValues with BeforeAndAfterAll with BeforeAndAfter {

  private lazy val log = LoggerFactory.getLogger(getClass)

  @Autowired private val cacheManager  : RedisCacheManager = null
  @Autowired private val userRepository: UserRepository    = null

  override def beforeAll() {
    // Spring Autowired 를 수행합니다.
    new TestContextManager(this.getClass).prepareTestInstance(this)
  }

  test("get cache") {
    cacheManager should not eq null
    val springCache = cacheManager.getCache("user")
    springCache should not eq null
  }

  test("spring cache get") {

    val user1 = userRepository.getUser("debop", 100)
    val user2 = userRepository.getUser("debop", 200)

    user1 should not eq null
    user1.favoriteMovies should not eq null
    user1.favoriteMovies.size should be > 0
    user1 shouldEqual user2
    user1.favoriteMovies.size shouldEqual user2.favoriteMovies.size
  }

  test("spring cache evict") {

    val userId = UUID.randomUUID().toString

    val user1 = userRepository.getUser(userId, 100)
    val user2 = userRepository.getUser(userId, 200)

    userRepository.updateUser(user1)

    val user3 = userRepository.getUser(userId, 200)

    user1 shouldEqual user2
    user1.favoriteMovies.size shouldEqual user2.favoriteMovies.size
    user3.favoriteMovies.size should not equal user1.favoriteMovies.size
  }

}
