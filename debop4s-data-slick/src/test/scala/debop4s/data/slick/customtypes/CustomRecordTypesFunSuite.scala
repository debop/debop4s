package debop4s.data.slick.customtypes

import debop4s.data.slick.AbstractSlickFunSuite
import debop4s.data.slick.SlickExampleDatabase._
import debop4s.data.slick.SlickExampleDatabase.driver.simple._

import scala.reflect.ClassTag
import scala.slick.lifted
import scala.util.Try

/**
 * DDD Component 또는 Record 형태의 정보를 매핑하는 테스트
 * @author sunghyouk.bae@gmail.com
 */
class CustomRecordTypesFunSuite extends AbstractSlickFunSuite {

  // custom record type
  case class Pair[A, B](a: A, b: B)

  final class PairShape[Level <: ShapeLevel, M <: Pair[_, _], U <: Pair[_, _] : ClassTag, P <: Pair[_, _]](val shapes: Seq[Shape[_, _, _, _]])
    extends MappedScalaProductShape[Level, Pair[_, _], M, U, P] {

    override def buildValue(elems: IndexedSeq[Any]): Any = Pair(elems(0), elems(1))
    override def copy(shapes: Seq[lifted.Shape[_ <: ShapeLevel, _, _, _]]): lifted.Shape[Level, _, _, _] =
      new PairShape(shapes)
  }

  implicit def pairShape[Level <: ShapeLevel, M1, M2, U1, U2, P1, P2]
  (implicit s1: Shape[_ <: Level, M1, U1, P1],
   s2: Shape[_ <: Level, M2, U2, P2]): PairShape[Level, Pair[M1, M2], Pair[U1, U2], Pair[P1, P2]] =
    new PairShape[Level, Pair[M1, M2], Pair[U1, U2], Pair[P1, P2]](Seq(s1, s2))

  class PairShapeTable(tag: Tag) extends Table[(String, Pair[Int, String])](tag, "pair_shape") {
    def name = column[String]("name")
    def shapeNo = column[Int]("shape_no")
    def shapeDesc = column[String]("shape_desc")

    def pair = Pair(shapeNo, shapeDesc)

    def * = (name, pair)
  }
  lazy val PairShapes = TableQuery[PairShapeTable]

  test("custom record type") {
    withSession { implicit session =>
      Try {PairShapes.ddl.drop}

      PairShapes.ddl.create

      PairShapes +=("triangle", Pair(1, "a"))
      PairShapes +=("circle", Pair(2, "b"))
      PairShapes +=("rectangle", Pair(3, "c"))

      PairShapes.filter(_.name === "rectangle".bind).map(_.pair.a).run shouldEqual Vector(3)

      val q =
        PairShapes
        .map(_.pair)
        .map { case p => Pair(p.a, p.b ++ p.b) }
        .filter { case Pair(id, _) => id =!= 1 }
        .sortBy { case Pair(_, ss) => ss }
        .map { case Pair(id, ss) => Pair(id, Pair(42, ss)) }

      q.run shouldEqual Vector(Pair(2, Pair(42, "bb")), Pair(3, Pair(42, "cc")))

    }
  }

}
