package debop4s.core.stests

import debop4s.core._
import java.util.UUID
import org.slf4j.LoggerFactory

/**
 * debop4s.core.tests.LocalTest
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 14. 오후 7:11
 */
class LocalMapTest extends AbstractCoreTest {

  override lazy val log = LoggerFactory.getLogger(getClass)

  before {
    LocalMap.clear()
  }

  test("put/get value type") {
    val key = "Local.Value.Key"
    val value = UUID.randomUUID().toString
    LocalMap.put(key, value)

    if (key.isWhitespace) fail("key is whitespace")
    val stored = LocalMap.get(key).getOrElse(null).asInstanceOf[String]
    assert(stored != null)
    assert(stored == value)
  }

  test("put/get reference type") {
    val key = "Local.Reference.Key"
    val user = new User("user", "P" + Thread.currentThread().getId, 1)
    LocalMap.put(key, user)
    Thread.sleep(5)

    val storedUser = LocalMap.get[User](key).getOrElse(null)
    assert(storedUser != null)
    assert(storedUser == user)
    assert(user.name == storedUser.name)
    assert(user.password == storedUser.password)
    assert(user.age == storedUser.age)
  }

  test("get or create if not exists") {
    val key = "Local.GetOrCreate.Key"
    val user = LocalMap.getOrCreate(key, {
      new User("user", "P" + Thread.currentThread().getId, 2)
    }).getOrElse(null)

    Thread.sleep(5)

    val storedUser = LocalMap.get[User](key).getOrElse(null)
    storedUser should not be null
    storedUser should be(user)
    storedUser.name should equal(user.name)
    storedUser.password should equal(user.password)
    storedUser.age should equal(user.age)
  }
}

case class User(name: String, password: String, age: Int)