package debop4s.core

import java.lang.{Double => JDouble, Float => JFloat, Integer => JInt, Long => JLong}

import debop4s.core.utils.Strings

/**
 * CorePackageFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class CorePackageFunSuite extends AbstractCoreFunSuite {

  test("int convert") {
    "1".asInt shouldBe 1
    1L.asInt shouldBe 1
    1.0.asInt shouldBe 1
  }

  test("asChar") {
    val one:java.lang.Character = 'A'
    val empty:java.lang.Character = null
    one.asChar shouldEqual 'A'
    empty.asChar shouldEqual '\u0000'
    'A'.asInt.asChar shouldEqual 'A'
  }

  test("asByte") {
    val one:java.lang.Byte = 0x01.toByte
    val empty:java.lang.Byte = null
    one.asByte shouldEqual 1
    empty.asByte shouldEqual 0
    12.asByte shouldEqual 12
  }

  test("asShort") {
    val one:java.lang.Short = 1.toShort
    val empty:java.lang.Short = null
    one.asShort shouldEqual 1
    empty.asShort shouldEqual 0
    "123".asShort shouldEqual 123
  }

  test("asInt") {
    val one:Integer = 1
    val empty:Integer = null
    one.asInt shouldEqual 1
    empty.asInt shouldEqual 0
    "123".asInt shouldEqual 123
  }

  test("asLong") {
    val one:JLong = 1L
    val empty:JLong = null
    one.asLong shouldEqual 1L
    empty.asLong shouldEqual 0L
    "123".asLong shouldEqual 123L
  }

  test("asFloat") {
    val one:JFloat = 1.23f
    val empty:JFloat = null
    one.asFloat shouldEqual 1.23f
    empty.asFloat shouldEqual 0.0f
    "123.04".asFloat shouldEqual 123.04f
  }

  test("asDouble") {
    val one:JDouble = 1.23d
    val empty:JDouble = null
    one.asDouble shouldEqual 1.23d
    empty.asDouble shouldEqual 0.0d
    "123.04".asDouble shouldEqual 123.04d
  }

  test("asString") {
    val one:Integer = 1
    val empty:Integer = null
    one.asString shouldEqual "1"
    empty.asString shouldEqual Strings.NULL_STR
    123.asString shouldEqual "123"
  }

  // TODO: asDateTime, asDate 에 대해 kesti 의 소스로 update 해야 함.

//  test("asDateTime") {
//    0.asDateTime() shouldEqual Some(new DateTime(0))
//    val now = DateTime.now
//    now.getMillis.asDateTime() shouldEqual Some(now)
//
//    val a = null:DateTime
//    a.asDateTime() shouldEqual None
//    None.asDateTime() shouldEqual None
//
//    new Date(now.getMillis).asDateTime() shouldEqual Some(now)
//
//    "2015-10-14".asDateTime() shouldEqual Some(new DateTime(2015, 10, 14, 0, 0))
//  }
//
//  test("asDate") {
//    0.asDate() shouldEqual Some(new Date(0))
//    val now = new Date()
//    now.getTime().asDate() shouldEqual Some(now)
//
//    val a = null:Date
//    a.asDate() shouldEqual None
//    None.asDate() shouldEqual None
//
//    new DateTime(now.getTime).asDate() shouldEqual Some(now)
//  }
}
