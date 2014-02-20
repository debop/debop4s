package com.github.debop4s.data.tests

import javax.persistence.EntityManagerFactory
import org.scalatest.FunSuite
import org.springframework.beans.factory.annotation.Autowired

/**
 * com.github.debop4s.data.tests.AbstractDataTest
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 11. 오후 10:50
 */
abstract class AbstractDataTest extends FunSuite {

    @Autowired val emf: EntityManagerFactory = null
}
