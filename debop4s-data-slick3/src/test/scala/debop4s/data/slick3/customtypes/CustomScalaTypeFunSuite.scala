package debop4s.data.slick3.customtypes

import debop4s.data.slick3.AbstractSlickFunSuite
import debop4s.data.slick3.TestDatabase._
import debop4s.data.slick3.TestDatabase.driver.api._

/**
 * CustomScalaTypeFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class CustomScalaTypeFunSuite extends AbstractSlickFunSuite {

  sealed trait Bool
  case object True extends Bool
  case object False extends Bool

  implicit def boolColumnType: driver.BaseColumnType[Bool] =
    MappedColumnType.base[Bool, Int](
      b => if (b == True) 1 else 0,
      i => if (i == 1) True else False
    )

  class A(tag: Tag) extends Table[(String, Bool)](tag, "custom_scala_type") {
    def name = column[String]("name")
    def isActive = column[Bool]("isActive")
    def * = (name, isActive)
  }
  lazy val as = TableQuery[A]

  before {
    commit {
      as.schema.drop.asTry >>
      as.schema.create
    }
  }
  after {
    commit { as.schema.drop.asTry }
  }

  test("custom scala type - Bool") {
    commit { as ++= Seq(("A", True), ("B", False)) }

    readonly { as.length.result } shouldBe 2
    readonly {
      as.filter(_.name === "A".bind).map(_.isActive).take(1).result
    }.head shouldEqual True

    readonly {
      as.filter(_.name === "B".bind).map(_.isActive).take(1).result
    }.head shouldEqual False

    // NOTE: Custom Type 을 WHERE 절에 넣으려면 컬럼 수형과 같은 수형으로 type casting 을 해야 합니다.
    // NOTE: 그럼 EncryptedString 은 Casting 보다 실제 값을 지정하면 된다.

    readonly { as.filter(_.isActive === True.asInstanceOf[Bool]).length.result } shouldBe 1
    readonly { as.filter(_.isActive === False.asInstanceOf[Bool]).length.result } shouldBe 1
  }

}
