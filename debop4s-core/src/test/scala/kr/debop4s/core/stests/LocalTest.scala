package kr.debop4s.core.stests

import java.util.UUID
import kr.debop4s.core.Local
import kr.debop4s.core.logging.Logger
import kr.debop4s.core.testing.Testing
import org.junit.Test
import org.scalatest.junit.AssertionsForJUnit

/**
 * kr.debop4s.core.tests.LocalTest
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 14. 오후 7:11
 */
class LocalTest extends AssertionsForJUnit {

    lazy val log = Logger[LocalTest]

    @Test
    def multithreadTest() {
        Testing.run(15) {
            saveAndLoadValueType()
            saveAndLoadReferenceType()
            getOrCreate()
        }
    }

    @Test
    def saveAndLoadValueType() {
        val key = "Local.Value.Key"
        val value = UUID.randomUUID().toString
        Local.put(key, value)
        assert(value == Local.get(key))
    }

    @Test
    def saveAndLoadReferenceType() {
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

    @Test
    def getOrCreate() {
        val key = "Local.GetOrCreate.Key"
        val user = Local.getOrCreate(key, () => new User("user", "P" + Thread.currentThread().getId, 2))
        Thread.sleep(5)

        val storedUser = Local.get(key).asInstanceOf[User]
        assert(storedUser != null)
        assert(user.name == storedUser.name)
        assert(user.password == storedUser.password)
        assert(user.age == storedUser.age)
    }

}

case class User(name: String, password: String, age: Int)