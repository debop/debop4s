package debop4s.mongo.music

import java.util

import org.hamcrest.CoreMatchers
import org.joda.time.DateTime
import org.junit.Assert
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.{Criteria, Query}

/**
 * AlbumRepositoryFunSuite
 * @author sunghyouk.bae@gmail.com 14. 10. 19.
 */
class AlbumRepositoryFunSuite extends AbstractMusicFunSuite {

  @Autowired val repository: AlbumRepository = null
  @Autowired val template: MongoTemplate = null

  before {
    log.debug(s"delete all albums")
    repository.deleteAll()
    super.init()
  }

  test("create albums") {
    repository.save(albums)
    assertSingleGruxAlbum(repository.findOne(bigWhiskey.id))
  }

  test("finds Album By Concrete TrackName") {
    repository.save(albums)
    assertSingleGruxAlbum(repository.findByTracksName("Grux"))
    val loaded = repository.findByTracksName("Foo")
    loaded.isEmpty shouldEqual true
  }

  test("finds All Albums By TrackNameLike") {
    repository.save(albums)
    assertBothAlbums(repository.findByTracksNameLike("*it*"))
  }

  test("finds Albums By TrackRating") {
    bigWhiskey.tracks.get(4).rating = Stars.FOUR
    repository.save(albums)
    assertSingleGruxAlbum(repository.findByTracksRatingGreaterThan(Stars.THREE))
    val loaded = repository.findByTracksRatingGreaterThan(Stars.FOUR)
    loaded.isEmpty shouldEqual true
  }

  test("utcTimeTest") {
    repository.save(albums)
    val time: DateTime = DateTime.now.minusYears(1).minusMinutes(5)
    val query: Query = Query.query(Criteria.where("publishDate").gte(time))
    val loaded = template.find(query, classOf[Album])
    loaded.size shouldEqual 1
  }

  private def assertSingleGruxAlbum(albums: util.List[Album]) {
    Assert.assertThat(albums, CoreMatchers.is(CoreMatchers.notNullValue))
    Assert.assertThat(albums.size, CoreMatchers.is(1))
    Assert.assertThat(albums.get(0), CoreMatchers.is(CoreMatchers.notNullValue(classOf[Album])))
    assertSingleGruxAlbum(albums.get(0))
  }
}
