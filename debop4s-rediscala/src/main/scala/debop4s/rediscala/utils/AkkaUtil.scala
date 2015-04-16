package debop4s.rediscala.utils

import java.lang.{Double => JDouble, Long => JLong}
import java.util.{List => JList, Map => JMap, Set => JSet}

import akka.util.ByteString
import com.google.common.collect.Lists
import debop4s.rediscala.MemberScore
import redis.api.Limit

import scala.collection.JavaConverters._

/**
 * Akka 사용을 위한 Utility object
 * @author debop created at 2014. 5. 2.
 */
object AkkaUtil {

  implicit val actorSystem = akka.actor.ActorSystem("rediscala")
  implicit val executor = actorSystem.dispatcher

  val NegativeInfinity = Limit(Double.NegativeInfinity)
  val PositiveInfinity = Limit(Double.PositiveInfinity)

  implicit def toUtf8Str(v: Any): String = v match {
    case Some(v: ByteString) => v.utf8String
    case Some(v: String) => v
    case v: ByteString => v.utf8String
    case v: String => v
    case null => ""
    case None => ""
    case _ => ""
  }

  implicit def toUtf8StrSeq(xs: Seq[Any]): Seq[String] = {
    val results = Lists.newArrayListWithCapacity[String](xs.length)
    var i = 0
    while (i < xs.length) {
      results add toUtf8Str(xs(i))
      i += 1
    }
    results.asScala
  }

  implicit def toUtf8StrSet(xs: Seq[Any]): Set[String] = {
    toUtf8StrSeq(xs).toSet
  }

  implicit def toMemberScore(xs: Seq[(ByteString, Double)]): Seq[MemberScore] = {
    val results = Lists.newArrayListWithCapacity[MemberScore](xs.length)
    var i = 0
    while (i < xs.length) {
      val (m, s) = xs(i)
      results add MemberScore(m.utf8String, s)
      i += 1
    }
    results.asScala
  }

  implicit def toUtf8StrList(xs: Seq[Any]): Seq[String] = {
    val results = Lists.newArrayListWithCapacity[String](xs.length)
    var i = 0
    while (i < xs.length) {
      results add toUtf8Str(xs(i))
      i += 1
    }
    results.asScala
  }

  implicit def toJLong(x: Option[Long]): JLong = x match {
    case Some(x: Long) => x
    case None => null.asInstanceOf[JLong]
  }

  implicit def toJDouble(x: Option[Double]): JDouble = x match {
    case Some(x: Double) => x
    case None => null.asInstanceOf[JDouble]
  }
}
