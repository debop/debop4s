package debop4s.data.slick3.customtypes

import debop4s.data.slick3.TestDatabase._
import debop4s.data.slick3.TestDatabase.driver.api._
import debop4s.data.slick3._
import slick.lifted

import scala.reflect.ClassTag

/**
 * DDD Component 또는 Record 형태의 정보를 매핑하는 테스트
 * @author sunghyouk.bae@gmail.com
 */
class CustomRecordTypeFunSuite extends AbstractSlickFunSuite {

  // custom record type
  case class Pair[A, B](a: A, b: B)

  class PairShape[Level <: ShapeLevel, M <: Pair[_, _], U <: Pair[_, _] : ClassTag, P <: Pair[_, _]](val shapes: Seq[Shape[_, _, _, _]])
    extends MappedScalaProductShape[Level, Pair[_, _], M, U, P] {
    override def buildValue(elems: IndexedSeq[Any]): Any = Pair(elems(0), elems(1))
    override def copy(shapes: Seq[lifted.Shape[_ <: ShapeLevel, _, _, _]]): lifted.Shape[Level, _, _, _] =
      new PairShape(shapes)
  }

  implicit def pairShape[Level <: ShapeLevel, M1, M2, U1, U2, P1, P2]
  (implicit s1: Shape[_ <: Level, M1, U1, P1], s2: Shape[_ <: Level, M2, U2, P2]): PairShape[Level, Pair[M1, M2], Pair[U1, U2], Pair[P1, P2]] =
    new PairShape[Level, Pair[M1, M2], Pair[U1, U2], Pair[P1, P2]](Seq(s1, s2))

  class PairShapes(tag: Tag) extends Table[(String, Pair[Int, String])](tag, "pair_shape") {
    def name = column[String]("name")
    def shapeNo = column[Int]("shape_no")
    def shapeDesc = column[String]("shape_desc")

    def pair = Pair(shapeNo, shapeDesc)
    def * = (name, pair)
  }
  lazy val pairShapes = TableQuery[PairShapes]

  before {
    commit {
      pairShapes.schema.drop.asTry >>
      pairShapes.schema.create
    }
  }
  after {
    commit { pairShapes.schema.drop }
  }

  test("custom record type") {
    commit {
      DBIO.seq(pairShapes +=("triangle", Pair(1, "a")),
               pairShapes +=("circle", Pair(2, "b")),
               pairShapes +=("rectangle", Pair(3, "c"))
      )
    }

    readonly { pairShapes.filter(_.name === "rectangle".bind).map(_.pair.a).result } shouldEqual Seq(3)

    val q = pairShapes
            .map(_.pair)
            .map { case p => Pair(p.a, p.b ++ p.b) }
            .filter { case Pair(id, _) => id =!= 1 }
            .sortBy { case Pair(_, ss) => ss }
            .map { case Pair(id, ss) => Pair(id, Pair(42, ss)) }

    readonly { q.result } shouldEqual Seq(Pair(2, Pair(42, "bb")), Pair(3, Pair(42, "cc")))
  }
}
