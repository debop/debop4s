package debop4s.data.mybatis

/**
 * package
 * @author sunghyouk.bae@gmail.com 15. 3. 19.
 */
package object mapping {

  type TypeHandler[T] = org.apache.ibatis.`type`.TypeHandler[T]

  type XSQL = scala.xml.Node

  implicit def string_to_xsql(s: String): XSQL = <xsql>
    {s}
  </xsql>
}
