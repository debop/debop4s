package com.github.debop4s.experiments.tests.async.postgresql

import com.github.debop4s.experiments.tests.AbstractExperimentTest
import com.github.mauricio.async.db.postgresql.PostgreSQLConnection
import com.github.mauricio.async.db.postgresql.util.URLParser
import com.github.mauricio.async.db.util.ExecutorServiceUtils.CachedExecutionContext
import scala.concurrent._
import scala.concurrent.duration._

/**
 * BasicExample
 * Created by debop on 2014. 3. 2.
 */
class AsyncPostgreSqlTest extends AbstractExperimentTest {

    test("basic example") {
        val configuration = URLParser.parse("jdbc:postgresql://localhost:5432/hibernate?user=root&password=root")
        val conn = new PostgreSQLConnection(configuration)

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
