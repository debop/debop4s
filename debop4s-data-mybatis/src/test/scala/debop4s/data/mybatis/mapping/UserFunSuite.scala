package debop4s.data.mybatis.mapping

import debop4s.data.mybatis.AbstractMyBatisFunSuite
import debop4s.data.mybatis.domain.User
import debop4s.data.mybatis.repository.UserRepository


/**
 * UserFunSuite
 * @author sunghyouk.bae@gmail.com 15. 3. 21.
 */
class UserFunSuite extends AbstractMyBatisFunSuite {

  test("insert user by User") {
    withReadOnly(Database.default) { implicit session =>
      val expected = User(0, "test", "example@example.com")
      UserRepository.create(expected)
      UserRepository.findById(expected.id) shouldEqual Some(expected)
    }
  }

}
