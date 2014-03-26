package com.github.debop4s.experiments.tests.activateframework

import PersistenceContext._
import com.github.debop4s.experiments.tests.AbstractExperimentTest
import net.fwbrasil.activate.ActivateContext
import net.fwbrasil.activate.storage.relational.PooledJdbcRelationalStorage
import net.fwbrasil.activate.storage.relational.idiom.{mySqlDialect, SqlIdiom}

object PersistenceContext extends ActivateContext {
    // override val storage = new TransientMemoryStorage

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

class Person(var name: String) extends Entity

class CreatePersonTableMigration extends Migration {
    def timestamp = 201403261605L

    def up = {
        table[Person].createTable(_.column[String]("name"))
    }
}

class ActivateFrameworkSamples extends AbstractExperimentTest {

    test("간단한 ActiveFramework 예제") {
        transactional {
            new Person("John")
        }

        val john = transactional {
            select[Person].where(_.name :== "John").head
        }

        transactional {
            john.name = "John Doe"
        }

        transactional {
            all[Person].foreach(_.delete)
        }
    }
}



