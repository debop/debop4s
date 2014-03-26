package com.github.debop4s.experiments.tests.activateframework

import net.fwbrasil.activate.ActivateContext
import net.fwbrasil.activate.storage.relational.PooledJdbcRelationalStorage
import net.fwbrasil.activate.storage.relational.idiom.{mySqlDialect, SqlIdiom}

/**
 * MySqlContext
 * @author Sunghyouk Bae
 */
object MySqlContext extends ActivateContext {

    override val storage = new PooledJdbcRelationalStorage {
        override val jdbcDriver = "com.mysql.jdbc.Driver"
        override val jdbcDataSource = Some("com.mysql.jdbc.jdbc2.optional.MysqlDataSource")
        override val url = "jdbc:mysql://localhost/activate_test"
        override val user = Some("root")
        override val password = Some("root")
        override val dialect: SqlIdiom = mySqlDialect
        override val poolSize = 100
    }

}
