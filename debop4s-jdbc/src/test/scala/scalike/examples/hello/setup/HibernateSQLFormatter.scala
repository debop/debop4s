package scalike.examples.hello.setup

import scalikejdbc.SQLFormatter

/**
 * HibernateSQLFormatter
 * @author sunghyouk.bae@gmail.com
 */
class HibernateSQLFormatter extends SQLFormatter {

  private lazy val formatter = new org.hibernate.engine.jdbc.internal.BasicFormatterImpl

  override def format(sql: String): String = {
    formatter.format(sql)
  }
}
