package debop4s.data.mybatis.mapping

/**
 * Abstract Key Generator
 * @author sunghyouk.bae@gmail.com at 15. 3. 20.
 */
trait KeyGenerator {
  var keyProeprty: String
  var keyColumn: String
}

/** JDBC 3 Key Generator implementation.
  * This uses JDBC generatedKeys
  * @param _column Column to be read from the generated keys resultset.
  * @param _property Property to be set with the generated key value.
  */
class JdbcGeneratedKey(private[this] val _column: String,
                       private[this] val _property: String) extends KeyGenerator {
  override var keyColumn: String = _column
  override var keyProeprty: String = _property

}

object JdbcGeneratedKey {

  def apply(column: String, property: String): JdbcGeneratedKey =
    new JdbcGeneratedKey(column, property)
}

/** Base class to define a native SQL Key generator.
  * @tparam R result type of the generated key.
  *           == Sample code ==
  *           {{{
  *                                    keyGenerator = new SqlGeneratedKey[Long] {
  *                                      keyProperty = "myId"
  *                                      def xsql = "SELECT currval('my_sequence')"
  *                                    }
  *           }}}
  */
abstract class SqlGeneratedKey[R: Manifest] extends KeyGenerator {

  /**
   * Any one of STATEMENT, PREPARED or CALLABLE.
   * This causes MyBatis to use Statement, PreparedStatement or CallableStatement respectively.
   * Default: PREPARED.
   */
  var statementType: StatementType = StatementType.PREPARED

  /**
   * Property to be set.
   */
  var keyProperty: String = "id"

  /**
   * Property to be set.
   */
  var keyColumn: String = null

  /**
   * If true then this statement will be executed before the main statement.
   */
  var executeBefore: Boolean = false

  /**
   * Returns the Class of the generated key.
   */
  val resultTypeClass = manifest[R].runtimeClass

  /** Dynamic SQL CODE to be executed in order to obtain/generate the key
    * == Code sample ==
    * {{{
    *   def xsql = "SELECT currval('my_sequence')"
    * }}}
    */
  def xsql: XSQL
}
