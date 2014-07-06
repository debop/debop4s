package debop4s.rediscala

import akka.util.ByteString
import debop4s.rediscala.protocol._

import scala.annotation.{implicitNotFound, tailrec}
import scala.collection.mutable
import scala.util.Try

trait MultiBulkConverter[A] {
  def to(redisReply: MultiBulk): Try[A]
}

object MultiBulkConverter {

  def toSeqString(reply: MultiBulk): Seq[String] = {
    reply.responses.map { r =>
      r.map(_.toString)
    }.getOrElse(Seq.empty)
  }

  def toSeqByteString[R](reply: MultiBulk)(implicit deserializer: ByteStringDeserializer[R]): Seq[R] = {
    reply.responses.map { r =>
      r.map(reply => deserializer.deserialize(reply.toByteString))
    }.getOrElse(Seq.empty)
  }

  def toSeqOptionByteString[R](reply: MultiBulk)(implicit deserializer: ByteStringDeserializer[R]): Seq[Option[R]] = {
    reply.responses.map { r =>
      r.map(_.asOptByteString.map(deserializer.deserialize))
    }.getOrElse(Seq.empty)
  }

  def toSeqTuple2ByteStringDouble[R](reply: MultiBulk)(implicit deserializer: ByteStringDeserializer[R]): Seq[(R, Double)] = {
    reply.responses.map {
      r => {
        val s = r.map(_.toByteString)
        val builder = Seq.newBuilder[(R, Double)]
        s.grouped(2).foreach {
          case Seq(a, b) => builder += ((deserializer.deserialize(a), b.utf8String.toDouble))
        }
        builder.result()
      }
    }.getOrElse(Seq.empty)
  }

  def toMapString(reply: MultiBulk): Map[String, String] = {
    reply.responses.map(bs => {
      val builder = Map.newBuilder[String, String]
      seqtoMapString(bs, builder)
      builder.result()
    }).getOrElse(Map.empty)
  }

  @tailrec
  private def seqtoMapString(bsSeq: Seq[RedisReply], acc: mutable.Builder[(String, String), Map[String, String]]): Unit = {
    if (bsSeq.nonEmpty) {
      acc += ((bsSeq.head.asOptByteString.map(_.utf8String).getOrElse(""), bsSeq.tail.head.asOptByteString.map(_.utf8String).getOrElse("")))
      seqtoMapString(bsSeq.tail.tail, acc)
    }
  }

  def toSeqMapString(reply: MultiBulk): Seq[Map[String, String]] = {
    reply.responses.map {
      s =>
        s.map({
          case m: MultiBulk => {
            m.responses.map {
              s =>
                val builder = Seq.newBuilder[(String, String)]
                s.grouped(2).foreach {
                  case Seq(a, b) => builder += ((a.toString, b.toString))
                }
                builder.result()
            }.getOrElse(Seq())
          }
          case _ => Seq()
        }).map {
          _.toMap
        }
    }.getOrElse(Seq.empty)
  }

  def toOptionStringByteString[R](reply: MultiBulk)(implicit deserializer: ByteStringDeserializer[R]): Option[(String, R)] = {
    reply.responses.map(r => {
      Some(r.head.toString, deserializer.deserialize(r.tail.head.toByteString))
    }).getOrElse(None)
  }

  def toSeqBoolean(reply: MultiBulk): Seq[Boolean] = {
    reply.responses.map(r => {
      r.map(_.toString == "1")
    }).getOrElse(Seq.empty)
  }


}

@implicitNotFound(msg = "No ByteString serializer found for type ${K}. Try to implement an implicit ByteStringSerializer for this type.")
trait ByteStringSerializer[K] {
  def serialize(data: K): ByteString
}

object ByteStringSerializer extends ByteStringSerializerLowPriority

trait ByteStringSerializerLowPriority {

  implicit object String extends ByteStringSerializer[String] {
    def serialize(key: String): ByteString = ByteString(key)
  }

  implicit object ShortConverter extends ByteStringSerializer[Short] {
    def serialize(i: Short): ByteString = ByteString(i.toString)
  }

  implicit object IntConverter extends ByteStringSerializer[Int] {
    def serialize(i: Int): ByteString = ByteString(i.toString)
  }

  implicit object LongConverter extends ByteStringSerializer[Long] {
    def serialize(i: Long): ByteString = ByteString(i.toString)
  }

  implicit object FloatConverter extends ByteStringSerializer[Float] {
    def serialize(f: Float): ByteString = ByteString(f.toString)
  }

  implicit object DoubleConverter extends ByteStringSerializer[Double] {
    def serialize(d: Double): ByteString = ByteString(d.toString)
  }

  implicit object CharConverter extends ByteStringSerializer[Char] {
    def serialize(c: Char): ByteString = ByteString(c)
  }

  implicit object ByteConverter extends ByteStringSerializer[Byte] {
    def serialize(b: Byte): ByteString = ByteString(b)
  }

  implicit object ArrayByteConverter extends ByteStringSerializer[Array[Byte]] {
    def serialize(b: Array[Byte]): ByteString = ByteString(b)
  }

  implicit object ByteStringConverter extends ByteStringSerializer[ByteString] {
    def serialize(bs: ByteString): ByteString = bs
  }
}

@implicitNotFound(msg = "No ByteString deserializer found for type ${T}. Try to implement an implicit ByteStringDeserializer for this type.")
trait ByteStringDeserializer[T] {
  def deserialize(bs: ByteString): T
}

object ByteStringDeserializer extends ByteStringDeserializerLowPriority

trait ByteStringDeserializerLowPriority extends ByteStringDeserializerDefault {

}

trait ByteStringDeserializerDefault {
  implicit object String extends ByteStringDeserializer[String] {
    def deserialize(bs: ByteString): String = bs.utf8String
  }
}

trait ByteStringFormatter[T] extends ByteStringSerializer[T] with ByteStringDeserializer[T]


@implicitNotFound(msg = "No RedisReplyDeserializer deserializer found for type ${T}. Try to implement an implicit RedisReplyDeserializer for this type.")
trait RedisReplyDeserializer[T] {
  def deserialize: PartialFunction[RedisReply, T]
}

object RedisReplyDeserializer extends RedisReplyDeserializerLowPriority

trait RedisReplyDeserializerLowPriority extends RedisReplyDeserializerDefault {

  implicit object RedisReply extends RedisReplyDeserializer[RedisReply] {
    def deserialize: PartialFunction[RedisReply, RedisReply] = {
      case reply => reply
    }
  }
}

trait RedisReplyDeserializerDefault {

  implicit object String extends RedisReplyDeserializer[String] {
    def deserialize: PartialFunction[RedisReply, String] = {
      case Bulk(Some(bs)) => bs.utf8String
    }
  }
}