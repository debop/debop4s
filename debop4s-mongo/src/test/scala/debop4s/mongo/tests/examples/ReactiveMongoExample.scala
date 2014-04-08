package debop4s.mongo.tests.examples

import debop4s.core.parallels.Asyncs
import debop4s.mongo.tests.AbstractMongoTest
import play.api.libs.iteratee.Iteratee
import reactivemongo.api._
import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.bson._
import scala.concurrent.ExecutionContext.Implicits.global


/**
 * ReactiveMongoExample
 * Created by debop on 2014. 3. 2.
 */
class ReactiveMongoExample extends AbstractMongoTest {

    test("connect mongodb") {
        val driver = MongoDriver()
        val connection = driver.connection(List("localhost"))

        val db = connection.db("musicDB")
        val collection = db[BSONCollection]("album")

        assert(db != null)
        assert(collection != null)
    }


    test("simple query") {
        val driver = MongoDriver()
        val connection = driver.connection(List("localhost"))

        val db = connection.db("musicDB")
        val collection = db[BSONCollection]("album")


        val query = BSONDocument("artist" -> "Dave Matthews Band")
        val filter = BSONDocument("publishDate" -> 1, "_id" -> 1)

        val future = collection
                     .find(query, filter)
                     .cursor[BSONDocument]
                     .enumerate()
                     .apply(Iteratee.foreach {
            doc => println("found document: " + BSONDocument.pretty(doc))
        })
        Asyncs.ready(future)
    }

    test("find all document") {
        val driver = MongoDriver()
        val connection = driver.connection(List("localhost"))

        val db = connection.db("musicDB")
        val collection = db[BSONCollection]("album")

        val query = BSONDocument()

        val futureList = collection.find(query).cursor[BSONDocument].collect[List]()

        val task = futureList.map {
            list =>
                list.foreach {
                    doc =>
                        println("found document: " + BSONDocument.pretty(doc))
                }
        }
        Asyncs.ready(task)
    }

}
