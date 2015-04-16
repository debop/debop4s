package debop4s.core.utils

import java.util.UUID

import debop4s.core.AbstractCoreFunSuite

/**
 * debop4s.core.tests.LocalFunSuite
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 14. 오후 7:11
 */
class LocalFunSuite extends AbstractCoreFunSuite {

  before { Local.clearAll() }

  test("put/get value kind") {
    val key = "Local.Value.Key"
    val value = UUID.randomUUID().toString
    Local.put(key, value)

    if (Strings.isWhitespace(key)) fail("key is whitespace")
    val stored = Local.get[String](key).orNull
    stored should not be null
    stored shouldEqual value
  }

  test("put/get reference kind") {
    val key = "Local.Reference.Key"
    val user = new User("user", "P" + Thread.currentThread().getId, 1)
    Local.put(key, user)
    Thread.sleep(5)

    val storedUser = Local.get[User](key).orNull
    // storedUser != null)
    storedUser shouldEqual user
    user.name shouldEqual storedUser.name
    user.password shouldEqual storedUser.password
    user.age shouldEqual storedUser.age
  }

  test("get or create if not exists") {
    val key = "Local.GetOrCreate.Key"
    val user = Local.getOrCreate(key, {
      new User("user", "P" + Thread.currentThread().getId, 2)
    }).orNull

    Thread.sleep(5)

    val storedUser = Local.get[User](key).orNull
    storedUser should not be null
    storedUser shouldEqual user
    storedUser.name shouldEqual user.name
    storedUser.password shouldEqual user.password
    storedUser.age shouldEqual user.age
  }
}

case class User(name: String, password: String, age: Int)