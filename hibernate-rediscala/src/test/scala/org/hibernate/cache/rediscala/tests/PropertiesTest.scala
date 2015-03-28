package org.hibernate.cache.rediscala.tests

import java.util.Properties
import org.scalatest.{ BeforeAndAfter, Matchers, FunSuite }

/**
 * org.hibernate.cache.rediscala.tests.PropertiesTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 21. 오후 5:02
 */
class PropertiesTest extends FunSuite with Matchers with BeforeAndAfter {

  test("load properties by class") {
    val props = new Properties()
    val cachePath = "/hibernate-redis.properties"

    val inputStream = getClass.getResourceAsStream(cachePath)
    inputStream should not equal ( null )
    props.load(inputStream)

    print("properties... " + props.toString)
  }

  test("load properties by classLoader") {
    val props = new Properties()
    val cachePath = "hibernate-redis.properties"

    val inputStream = getClass.getClassLoader.getResourceAsStream(cachePath)
    inputStream should not equal ( null )
    props.load(inputStream)

    print("properties... " + props.toString)
  }

}
