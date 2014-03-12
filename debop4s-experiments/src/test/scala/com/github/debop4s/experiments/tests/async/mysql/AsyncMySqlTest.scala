package com.github.debop4s.experiments.tests.async.mysql

import com.github.debop4s.experiments.tests.AbstractExperimentTest
import com.github.mauricio.async.db.Configuration
import com.github.mauricio.async.db.mysql.MySQLConnection
import com.github.mauricio.async.db.util.ExecutorServiceUtils.CachedExecutionContext
import scala.concurrent._
import scala.concurrent.duration._

/**
 * BasicExample
 * Created by debop on 2014. 3. 2.
 */
class AsyncMySqlTest extends AbstractExperimentTest {

    test("basic example") {
        val configuration = new Configuration(host = "localhost",
            username = "root",
            password = Some("root"),
            database = Some("hibernate"),
            port = 3306)
        val conn = new MySQLConnection(configuration)

        Await.ready(conn.connect, 5 seconds)

        val future = conn.sendQuery("SELECT 0")

        val mapResult = future.map(queryResult =>
            queryResult.rows match {
                case Some(resultSet) => {
                    val row = resultSet.head
                    row(0)
                }
                case None => -1
            })

        val result = Await.result(mapResult, 5 seconds)

        println(result)

        conn.disconnect
    }

}
