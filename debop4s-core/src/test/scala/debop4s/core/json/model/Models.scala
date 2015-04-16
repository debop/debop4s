package debop4s.core.json.model

import org.joda.time.DateTime

object Models {

  case class ConstructorTestCaseClass(intValue: Int, stringValue: String)

  case class Project(name: String, startDate: DateTime, lang: Option[Language], teams: List[Team])

  case class Language(name: String, version: Double)

  case class Team(role: String, members: List[Employee])

  case class Employee(name: String, experience: Int)

  case class Nullable(name: String)

  case class Primitives(v1: Int, v2: Long, v3: Double, v4: Float, v5: String, v6: Int, v7: Byte, v8: Boolean)

  case class Ints(x: List[List[Int]] = List(List[Int]()))

  case class Rec(n: Int, xs: List[Rec])

  case class Members(x: String, y: Int) {
    val foo1 = "foo"
    lazy val foo2 = "foo"
  }

  case class Meeting(place: String, time: DateTime)

  case class Times(times: List[DateTime])

  sealed abstract class Bool

  case class True() extends Bool

  case class False() extends Bool

  case class Ambiguous(child: Bool)

  trait Bird

  case class Falcon(weight: Double) extends Bird

  case class Chicken(eggs: Int) extends Bird

  case class AmbiguousP(bird: Bird)

  case class OptionOfAmbiguous(opt: Option[Bool])

  case class OptionOfAmbiguousP(opt: Option[Bird])

  case class SetContainer(set: Set[String])

  case class ArrayContainer(array: Array[String])

  case class SeqContainer(seq: Seq[String])

  case class OptionOfTupleOfDouble(position: Option[(Double, Double)])

  case class Player(name: String)

  case class TypeConstructor[A](x: A)

  case class ProperType(x: TypeConstructor[Chicken], t: (Int, Player))

  case class PlayerWithDefault(name: String, credits: Int = 5)

  case class PlayerWithOptionDefault(name: String, score: Option[Int] = Some(6))

  case class Gimmick(name: String)

  case class PlayerWithGimmick(name: String, gimmick: Gimmick = Gimmick("default"))

  case class PlayerWithBird(name: String, bird: Bird = Chicken(3))

  case class PlayerWithList(name: String, badges: List[String] = List("intro", "tutorial"))

  case class MeetingWithDefault(place: String, time: DateTime = new DateTime(7777L))

  case class TimesWithDefault(times: List[DateTime] = List(new DateTime(8888L)))
}
