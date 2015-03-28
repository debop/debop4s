package debop4s.data.slick.customtypes

import debop4s.data.slick.AbstractSlickFunSuite
import debop4s.data.slick.SlickExampleDatabase._
import debop4s.data.slick.SlickExampleDatabase.driver.simple._

import scala.util.Try

/**
 * Scala 의 사용자 정의 Type 에 대해 `MappedColumnType` 을 이용한 처리
 * @author sunghyouk.bae@gmail.com
 */
class CustomScalaTypesFunSuite extends AbstractSlickFunSuite {

  sealed trait Bool
  case object True extends Bool
  case object False extends Bool

  implicit def boolColumnType =
    MappedColumnType.base[Bool, Int](
                                      b => if (b == True) 1 else 0,
                                      i => if (i == 0) False else True
                                    )

  class A(tag: Tag) extends Table[(String, Bool)](tag, "custom_scala_type") {
    def name = column[String]("name")
    def isActive = column[Bool]("isActive")
    def * = (name, isActive)
  }
  lazy val As = TableQuery[A]

  test("custom scala type - Bool") {
    withSession { implicit session =>
      Try { As.ddl.drop }
      As.ddl.create

      As ++= Seq(("A", True), ("B", False))

      As.length.run shouldEqual 2

      As.filter(_.name === "A".bind).map(_.isActive).first shouldEqual True
      As.filter(_.name === "B".bind).map(_.isActive).first shouldEqual False

      // NOTE: Custom Type 을 WHERE 절에 넣으려먼 컬럼 수형과 같은 수형으로 type casting을 해야 합니다.
      // NOTE: 그럼 EncryptedString 은???
      As.filter(_.isActive === True.asInstanceOf[Bool]).length.run shouldEqual 1
      As.filter(_.isActive === False.asInstanceOf[Bool]).length.run shouldEqual 1

      As.delete
    }
  }
}
