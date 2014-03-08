package com.github.debop4s.core.stests

import org.scalatest._
import org.slf4j.LoggerFactory

/**
 * AbstractCoreTest
 * Created by debop on 2014. 2. 22.
 */
abstract class AbstractCoreTest extends FunSuite with Matchers with BeforeAndAfter {

  lazy val log = LoggerFactory.getLogger(getClass)

}
