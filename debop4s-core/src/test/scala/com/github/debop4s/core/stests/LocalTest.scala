package com.github.debop4s.core.stests

import com.github.debop4s.core._
import com.github.debop4s.core.Local
import java.util.UUID
import org.slf4j.LoggerFactory

/**
 * com.github.debop4s.core.tests.LocalTest
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 14. 오후 7:11
 */
class LocalTest extends AbstractCoreTest {

    override lazy val log = LoggerFactory.getLogger(getClass)

    before {
        Local.clear()
    }

    test("put/get value type") {
        val key = "Local.Value.Key"
        val value = UUID.randomUUID().toString
        Local.put(key, value)

        if (key.isWhitespace) fail("key is whitespace")
        val stored = Local.get(key).getOrElse(null).asInstanceOf[String]
        assert(stored != null)
        assert(stored == value)
    }

    test("put/get reference type") {
        val key = "Local.Reference.Key"
        val user = new User("user", "P" + Thread.currentThread().getId, 1)
        Local.put(key, user)
        Thread.sleep(5)

        val storedUser = Local.get[User](key).getOrElse(null)
        assert(storedUser != null)
        assert(storedUser == user)
        assert(user.name == storedUser.name)
        assert(user.password == storedUser.password)
        assert(user.age == storedUser.age)
    }

    test("get or create if not exists") {
        val key = "Local.GetOrCreate.Key"
        val user = Local.getOrCreate(key, {
            new User("user", "P" + Thread.currentThread().getId, 2)
        }).getOrElse(null)

        Thread.sleep(5)

        val storedUser = Local.get[User](key).getOrElse(null)
        storedUser should not be null
        storedUser should be(user)
        storedUser.name should equal(user.name)
        storedUser.password should equal(user.password)
        storedUser.age should equal(user.age)
    }
}

case class User(name: String, password: String, age: Int)