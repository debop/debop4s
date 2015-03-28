package debop4s.data.slick.examples

import debop4s.data.slick.AbstractSlickFunSuite
import debop4s.data.slick.SlickExampleDatabase._
import debop4s.data.slick.SlickExampleDatabase.driver.simple._

import scala.util.Try


case class MyMappedId(value: Int) extends AnyVal with scala.slick.lifted.MappedTo[Int]

/**
 * RelationalMapperFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class RelationalMapperFunSuite extends AbstractSlickFunSuite {

  sealed trait Bool
  case object True extends Bool
  case object False extends Bool

  sealed trait EnumType
  case object EnumValue1 extends EnumType
  case object EnumValue2 extends EnumType
  case object EnumValue3 extends EnumType

  test("MappedColumnType 을 이용한 수형 변환") {
    implicit val bool2IntTypeMapper =
      MappedColumnType.base[Bool, Int](
                                        b => if (b == True) 1 else 0,
                                        i => if (i == 0) False else True
                                      )

    class T(tag: Tag) extends Table[(Int, Bool, Option[Bool])](tag, "mapped_bool_int_t") {
      def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
      def b = column[Bool]("b")
      def c = column[Option[Bool]]("c")
      def * = (id, b, c)
    }
    val ts = TableQuery[T]

    withSession { implicit session =>
      Try { ts.ddl.drop }
      ts.ddl.create

      ts.map(r => (r.b, r.c)) ++= Seq((False, None), (True, Some(True)))
      ts.run.toSet shouldEqual Set((1, False, None), (2, True, Some(True)))
      ts.filter(_.b === ( True: Bool )).run.toSet shouldEqual Set((2, True, Some(True)))
      ts.filter(_.b === ( False: Bool )).run.toSet shouldEqual Set((1, False, None))
    }
  }

  test("mapped type 2") {

    implicit val enumTypeMapper =
      MappedColumnType.base[EnumType, Char](
      {
        case EnumValue1 => 'A'
        case EnumValue2 => 'B'
        case _ => 'C'
      }, {
        case 'A' => EnumValue1
        case 'B' => EnumValue2
        case _ => EnumValue3
      })

    class T(tag: Tag) extends Table[(Int, EnumType, Option[EnumType])](tag, "mapped_enumtype_t2") {
      def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
      def b = column[EnumType]("b")
      def c = column[Option[EnumType]]("c")
      def * = (id, b, c)
    }
    val ts = TableQuery[T]

    withSession { implicit session =>
      Try { ts.ddl.drop }
      ts.ddl.create

      ts.map(x => (x.b, x.c)) ++= Seq((EnumValue1, None), (EnumValue1, Some(EnumValue2)), (EnumValue2, Some(EnumValue3)))
      ts.run.toSet shouldEqual Set((1, EnumValue1, None), (2, EnumValue1, Some(EnumValue2)), (3, EnumValue2, Some(EnumValue3)))
      ts.filter(_.b === ( EnumValue1: EnumType )).run.toSet shouldEqual Set((1, EnumValue1, None), (2, EnumValue1, Some(EnumValue2)))
      ts.filter(_.b === ( EnumValue2: EnumType )).run.toSet shouldEqual Set((3, EnumValue2, Some(EnumValue3)))
    }
  }

  test("mapped ref type") {
    implicit val bool2StringTypeMapper =
      MappedColumnType.base[Bool, String](
                                           b => if (b == True) "y" else "n",
                                           s => if (s == "y") True else False
                                         )

    class T(tag: Tag) extends Table[(Int, Bool, Option[Bool])](tag, "mapped_bool_string_t") {
      def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
      def b = column[Bool]("b")
      def c = column[Option[Bool]]("c")
      def * = (id, b, c)
    }
    val ts = TableQuery[T]

    withSession { implicit session =>
      Try { ts.ddl.drop }
      ts.ddl.create

      ts.map(r => (r.b, r.c)) ++= Seq((False, None), (True, Some(True)))
      ts.run.toSet shouldEqual Set((1, False, None), (2, True, Some(True)))
      ts.filter(_.b === ( True: Bool )).run.toSet shouldEqual Set((2, True, Some(True)))
      ts.filter(_.b === ( False: Bool )).run.toSet shouldEqual Set((1, False, None))
    }
  }

  test("auto mapped") {
    class T(tag: Tag) extends Table[(MyMappedId, Int)](tag, "mapped_automapped_t2") {
      def id = column[MyMappedId]("id", O.PrimaryKey)
      def v = column[Int]("v")
      def * = (id, v)
    }
    val ts = TableQuery[T]

    withSession { implicit session =>
      Try { ts.ddl.drop }
      ts.ddl.create

      ts ++= Seq((MyMappedId(1), 2), (MyMappedId(3), 4))
      ts.run.toSet shouldEqual Set((MyMappedId(1), 2), (MyMappedId(3), 4))
      ts.filter(_.id === MyMappedId(1)).run.toSet shouldEqual Set((MyMappedId(1), 2))
    }
  }
}
