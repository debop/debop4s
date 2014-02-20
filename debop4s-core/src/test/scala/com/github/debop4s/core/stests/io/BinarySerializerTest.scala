package com.github.debop4s.core.stests.io

import com.github.debop4s.core.io.BinarySerializer
import com.github.debop4s.core.stests.io.model.{User, Company}
import org.scalatest.{BeforeAndAfter, Matchers, FunSuite}
import org.slf4j.LoggerFactory

/**
 * com.github.debop4s.core.tests.io.BinarySerializerTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 15. 오후 8:06
 */
class BinarySerializerTest extends FunSuite with Matchers with BeforeAndAfter {

    lazy val log = LoggerFactory.getLogger(getClass)

    test("serialize/deserialize") {
        val ser = new BinarySerializer()
        val yearWeek = new YearWeek(2000, 1)
        val copied = ser.deserialize[YearWeek](ser.serialize(yearWeek), classOf[YearWeek])

        assert(copied != null)
        assert(copied.equals(yearWeek))
    }

    test("deep serialize reference type") {
        val serializer: BinarySerializer = new BinarySerializer
        val company: Company = new Company
        company.code = "HCT"
        company.name = "HealthConnect"
        company.amount = 10000L
        company.description = "헬스커넥트"

        for (i <- 0 until 100) {
            val user = new User()
            user.name = "USER_" + i
            user.empNo = "EMPNO_" + i
            user.address = "ADDR_" + i
            company.users += user
        }
        log.debug(s"user count = ${company.users.size}")

        val copied: Company = serializer.deserialize[Company](serializer.serialize(company), classOf[Company])
        assert(copied != null)
        assert(copied.users.size == 100)
        assert(copied == company)

        for (i <- 0 until copied.users.size) {
            assert(copied.users(i) == company.users(i))
        }
    }
}

case class YearWeek(year: Int = 0, week: Int = 1)
