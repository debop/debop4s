package debop4s.data.slick3.customtypes

import debop4s.data.slick3.AbstractSlickFunSuite
import debop4s.data.slick3._
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

  test("custom scala type - Bool") {
    Seq(
      as.schema.create,
      as ++= Seq(("A", True), ("B", False))
    ).run

    as.length.run shouldBe 2

    /*
    ┇ select x2.x3
    ┇ from (
    ┇   select x4."isActive" as x3
    ┇   from "custom_scala_type" x4
    ┇   where x4."name" = ?
    ┇   limit 1
    ┇ ) x2
     */
    as.filter(_.name === "A".bind).map(_.isActive).take(1).run.head shouldEqual True
    /*
    ┇ select x2.x3
    ┇ from (
    ┇   select x4."isActive" as x3
    ┇   from "custom_scala_type" x4
    ┇   where x4."name" = ?
    ┇   limit 1
    ┇ ) x2
     */
    as.filter(_.name === "B".bind).map(_.isActive).take(1).run.head shouldEqual False

    // NOTE: Custom Type 을 WHERE 절에 넣으려면 컬럼 수형과 같은 수형으로 type casting 을 해야 합니다.
    // NOTE: 그럼 EncryptedString 은 Casting 보다 실제 값을 지정하면 된다.

    /*
    ┇ select x2.x3
    ┇ from (
    ┇   select count(1) as x3
    ┇   from (
    ┇     select x4."name" as x5, x4."isActive" as x6
    ┇     from "custom_scala_type" x4
    ┇     where x4."isActive" = 1
    ┇   ) x7
    ┇ ) x2
     */
    as.filter(_.isActive === True.asInstanceOf[Bool]).length.run shouldBe 1

    /*
    ┇ select x2.x3
    ┇ from (
    ┇   select count(1) as x3
    ┇   from (
    ┇     select x4."name" as x5, x4."isActive" as x6
    ┇     from "custom_scala_type" x4
    ┇     where x4."isActive" = 0
    ┇   ) x7
    ┇ ) x2
     */
    as.filter(_.isActive === False.asInstanceOf[Bool]).length.run shouldBe 1

    as.schema.drop.run
  }

}
