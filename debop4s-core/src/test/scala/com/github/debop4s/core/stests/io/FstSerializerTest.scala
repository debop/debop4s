package com.github.debop4s.core.stests.io

import com.github.debop4s.core.stests.{YearWeek, AbstractCoreTest}
import org.slf4j.LoggerFactory
import com.github.debop4s.core.io.FstSerializer
import com.github.debop4s.core.stests.io.model.{User, Company}

/**
 * FstSerializerTest
 * @author Sunghyouk Bae
 */
class FstSerializerTest extends AbstractCoreTest {

    override lazy val log = LoggerFactory.getLogger(getClass)

    val ser = new FstSerializer()

    test("serialize/deserialize") {

        val yearWeek = new YearWeek(2000, 1)
        val copied = ser.deserialize[YearWeek](ser.serialize(yearWeek), classOf[YearWeek])

        assert(copied != null)
        assert(copied.equals(yearWeek))
    }

    test("deep serialize reference type") {
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

        val copied: Company = ser.deserialize[Company](ser.serialize(company), classOf[Company])
        assert(copied != null)
        assert(copied.users.size == 100)
        assert(copied == company)

        for (i <- 0 until copied.users.size) {
            assert(copied.users(i) == company.users(i))
        }
    }

}
