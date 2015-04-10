package debop4s.data.slick3.tests

import debop4s.data.slick3.TestDatabase.driver.api._
import debop4s.data.slick3.{AbstractSlickFunSuite, _}

import scala.language.existentials

/**
 * RelationalMapperFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class RelationalMapperFunSuite extends AbstractSlickFunSuite {

  import scala.concurrent.ExecutionContext.Implicits.global

  test("mapped type") {
    sealed trait Bool
    case object True extends Bool
    case object False extends Bool

    implicit val boolTypeMapper = MappedColumnType.base[Bool, Int](
    { b =>
      assert(b != null)
      if (b == True) 1 else 0
    }, { i =>
      if (i == 1) True else False
    })

    class T(tag: Tag) extends Table[(Int, Bool, Option[Bool])](tag, "t2") {
      def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
      def b = column[Bool]("b")
      def c = column[Option[Bool]]("c")
      def * = (id, b, c)
    }
    lazy val ts = TableQuery[T]

    db.seq(
      ts.schema.drop.asTry,
      ts.schema.create,
      ts.map(t => (t.b, t.c)) ++= Seq((False, None), (True, Some(True)))
    )
    ts.to[Set].run shouldEqual Set((1, False, None), (2, True, Some(True)))
    ts.filter(_.b === (True: Bool)).to[Set].run shouldEqual Set((2, True, Some(True)))
    ts.filter(_.b === (False: Bool)).to[Set].run shouldEqual Set((1, False, None))
    ts.schema.drop.run

  }

  test("mapped type - char") {
    sealed trait EnumType
    case object EnumValue1 extends EnumType
    case object EnumValue2 extends EnumType
    case object EnumValue3 extends EnumType

    implicit val enumTypeMapper = MappedColumnType.base[EnumType, Char](
    {
      case EnumValue1 => 'A'
      case EnumValue2 => 'B'
      case _ => 'C'
    }, {
      case 'A' => EnumValue1
      case 'B' => EnumValue2
      case _ => EnumValue3
    })

    class T(tag: Tag) extends Table[(Int, EnumType, Option[EnumType])](tag, "t32") {
      def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
      def b = column[EnumType]("b")
      def c = column[Option[EnumType]]("c")
      def * = (id, b, c)
    }
    lazy val ts = TableQuery[T]

    db.seq(
      ts.schema.drop.asTry,
      ts.schema.create,
      ts.map(t => (t.b, t.c)) ++= Seq((EnumValue1, None), (EnumValue1, Some(EnumValue2)), (EnumValue2, Some(EnumValue3)))
    )
    ts.to[Set].run shouldEqual Set((1, EnumValue1, None), (2, EnumValue1, Some(EnumValue2)), (3, EnumValue2, Some(EnumValue3)))
    ts.filter(_.b === (EnumValue1: EnumType)).to[Set].run shouldEqual Set((1, EnumValue1, None), (2, EnumValue1, Some(EnumValue2)))
    ts.filter(_.b === (EnumValue2: EnumType)).to[Set].run shouldEqual Set((3, EnumValue2, Some(EnumValue3)))
    ts.schema.drop.run
  }

  test("mapped ref") {
    sealed trait Bool
    case object True extends Bool
    case object False extends Bool

    implicit val boolTypeMapper = MappedColumnType.base[Bool, String](
    { b =>
      assert(b != null)
      if (b == True) "y" else "n"
    }, { s =>
      if (s == "y") True else False
    })

    class T(tag: Tag) extends Table[(Int, Bool, Option[Bool])](tag, "t2") {
      def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
      def b = column[Bool]("b")
      def c = column[Option[Bool]]("c")
      def * = (id, b, c)
    }
    lazy val ts = TableQuery[T]

    db.seq(
      ts.schema.drop.asTry,
      ts.schema.create,
      ts.map(t => (t.b, t.c)) ++= Seq((False, None), (True, Some(True)))
    )

    ts.to[Set].run shouldEqual Set((1, False, None), (2, True, Some(True)))
    ts.filter(_.b === (True: Bool)).to[Set].run shouldEqual Set((2, True, Some(True)))
    ts.filter(_.b === (False: Bool)).to[Set].run shouldEqual Set((1, False, None))

    ts.schema.drop.run

  }

  test("auto mapped") {
    class T(tag: Tag) extends Table[(MyMappedID, Int)](tag, "automapped_t") {
      def id = column[MyMappedID]("id", O.PrimaryKey)
      def v = column[Int]("v")
      def * = (id, v)
    }
    lazy val ts = TableQuery[T]

    db.seq(
      ts.schema.drop.asTry,
      ts.schema.create,
      ts ++= Seq((MyMappedID(1), 2), (MyMappedID(3), 4))
    )
    ts.to[Set].run shouldEqual Set((MyMappedID(1), 2), (MyMappedID(3), 4))
    ts.filter(_.id === MyMappedID(1)).to[Set].run shouldEqual Set((MyMappedID(1), 2))

    ts.schema.drop.run
  }

  def mappedToMacroCompilerBug = {
    case class MyId(val value: Int) extends MappedTo[Int]
    class MyTable(tag: Tag) extends Table[MyId](tag, "table") {
      def * = ???
    }

    // implicitly 는 implicit 로 전달받는 인자를 정의한 함수를 implicit 변수를 다른 방법으로 표현하는 것입니다.
    //
    /*
    class Pair[T: Ordering](val first: T, val second: T) {
      def smaller(implicit ord:Ordering[T]) =
        if(ord.compare(first, second) < 0) first else second

      def smaller2 =
        if(implicitly[Ordering[T]].compare(first, second) < 0) first else second
    }
    */

    implicitly[Shape[_ <: FlatShapeLevel, MyTable, _, _]]
    TableQuery(new MyTable(_)).map(identity)
  }

}

case class MyMappedID(value: Int) extends AnyVal with slick.lifted.MappedTo[Int]