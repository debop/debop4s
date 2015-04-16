package debop4s.mongo.music

import org.springframework.data.mongodb.core.query.Criteria._
import org.springframework.data.mongodb.core.query.{BasicQuery, Criteria, Query}

/**
 * AlbumIntegrationFunSuite
 * @author sunghyouk.bae@gmail.com 14. 10. 19.
 */
class AlbumIntegrationFunSuite extends AbstractMusicFunSuite {

  override def beforeAll() {
    super.beforeAll()
    operations.insertAll(albums)
  }

  test("lookup Album by Id with QueryBuilder") {
    val query = Query.query(Criteria.where("_id").is(bigWhiskey.id))
    assertSingleGruxAlbum(query)
  }

  //  test("lookup Album by Id using Json") {
  //    val query: Query = parseQuery("{ '_id' : { '$oid' : '%s' } }", bigWhiskey.id)
  //    assertSingleGruxAlbum(query)
  //  }

  test("look up Albums by TrackName using Json") {
    val query: Query = parseQuery("{'tracks.name' : 'Wheels'}")
    assertSinglePursuitAlbum(query)
  }
  test("lookup Album By TrackName Using QueryBuilder") {
    val spec = Query.query(where("tracks.name").is("Grux"))
    assertSingleGruxAlbum(spec)
  }

  test("lookup Album By TrackName Pattern") {
    val query: Query = parseQuery("{ 'tracks.name' : { '$regex' : '.*it.*' , '$options' : '' }}")
    assertBothAlbums(operations.find(query, classOf[Album], COLLECTION_NAME))
  }
  test("lookup Album By TrackName Pattern Using QueryBuilder") {
    val query = Query.query(where("tracks.name").regex(".*it.*"))
    assertBothAlbums(operations.find(query, classOf[Album], COLLECTION_NAME))
  }

  private def parseQuery(query: String, arguments: AnyRef*): Query = {
    new BasicQuery(String.format(query, arguments))
  }
}
