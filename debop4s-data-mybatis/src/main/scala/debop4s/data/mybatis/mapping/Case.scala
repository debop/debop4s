package debop4s.data.mybatis.mapping

/**
 * A result map reference based on some discriminator value.
 * @see [[org.mybatis.scala.mapping.ResultMap]]
 * @param value A discriminator value
 * @param resultMap A ResultMap to be used if value matches
 *
 * @author sunghyouk.bae@gmail.com 15. 3. 19.
 */
case class Case(value: String, resultMap: ResultMap[_])
