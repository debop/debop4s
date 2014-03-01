package com.github.debop4s.core.stests

import com.github.debop4s.core.Local
import java.util.UUID
import org.scalatest._
import org.slf4j.LoggerFactory

/**
 * com.github.debop4s.core.tests.LocalTest
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 14. 오후 7:11
 */
class LocalTest extends FunSuite with Matchers with BeforeAndAfter {

    lazy val log = LoggerFactory.getLogger(getClass)

    before {
        Local.clear()
    }

    test("put/get value type") {
        val key = "Local.Value.Key"
        val value = UUID.randomUUID().toString
        Local.put(key, value)
        assert(value == Local.get(key))
    }

    test("put/get reference type") {
        val key = "Local.Reference.Key"
        val user = new User("user", "P" + Thread.currentThread().getId, 1)
        Local.put(key, user)
        Thread.sleep(5)

        val storedUser = Local.get(key).asInstanceOf[User]
        assert(storedUser != null)
        assert(storedUser == user)
        assert(user.name == storedUser.name)
        assert(user.password == storedUser.password)
        assert(user.age == storedUser.age)
    }

    test("get or create if not exists") {
        val key = "Local.GetOrCreate.Key"
        val user = Local.getOrCreate(key, {new User("user", "P" + Thread.currentThread().getId, 2)})
        Thread.sleep(5)

        val storedUser = Local.get(key).asInstanceOf[User]
        storedUser should not be null
        storedUser should be(user)
        storedUser.name should equal(user.name)
        storedUser.password should equal(user.password)
        storedUser.age should equal(user.age)
    }
}

case class User(name: String, password: String, age: Int)