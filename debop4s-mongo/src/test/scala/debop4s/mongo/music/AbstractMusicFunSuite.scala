package debop4s.mongo.music

import java.util

import debop4s.mongo.AbstractMongoFunSuite
import org.joda.time.DateTime
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query
import org.springframework.test.context.support.AnnotationConfigContextLoader
import org.springframework.test.context.{ContextConfiguration, TestContextManager}

import scala.collection.JavaConverters._

/**
 * AbstractMusicFunSuite
 * @author sunghyouk.bae@gmail.com 14. 10. 19.
 */
@ContextConfiguration(classes = Array(classOf[MusicMongoConfiguration]),
  loader = classOf[AnnotationConfigContextLoader])
abstract class AbstractMusicFunSuite extends AbstractMongoFunSuite {

  val COLLECTION_NAME = "album"

  @Autowired val operations: MongoTemplate = null

  protected var bigWhiskey: Album = _
  protected var thePursuit: Album = _

  protected var albums = new util.ArrayList[Album]

  override def beforeAll() {
    // NOTE: TestContextManager#prepareTestInstance 를 실행시켜야 제대로 Dependency Injection이 됩니다.
    new TestContextManager(this.getClass).prepareTestInstance(this)
    init()
  }

  protected def init() {
    operations.dropCollection(COLLECTION_NAME)
    albums = new util.ArrayList[Album]

    bigWhiskey = new Album("Big Whiskey and the Groo Grux King", "Dave Matthews Band")
    bigWhiskey.add(new Track(0, "Grux"))
    bigWhiskey.add(new Track(1, "Shake me lika a monkey"))
    bigWhiskey.add(new Track(2, "Funny the way it is"))
    bigWhiskey.add(new Track(3, "Lying in the hands of God"))
    bigWhiskey.add(new Track(4, "Why I am"))
    bigWhiskey.add(new Track(5, "Dive in"))
    bigWhiskey.add(new Track(6, "Spaceman"))
    bigWhiskey.add(new Track(7, "Squirm"))
    bigWhiskey.add(new Track(8, "Alligator pie"))
    bigWhiskey.add(new Track(9, "Seven"))
    bigWhiskey.add(new Track(10, "Time bomb"))
    bigWhiskey.add(new Track(11, "My baby blue"))
    bigWhiskey.add(new Track(12, "You and me"))

    bigWhiskey.publishDate = DateTime.now.minusYears(1)

    albums.add(bigWhiskey)

    thePursuit = new Album("The Pursuit", "Jamie Cullum")
    thePursuit.add(new Track(0, "Just one of those things"))
    thePursuit.add(new Track(1, "I'm all over it"))
    thePursuit.add(new Track(2, "Wheels"))
    thePursuit.add(new Track(3, "If I ruled the world"))
    thePursuit.add(new Track(4, "You and me are gone"))
    thePursuit.add(new Track(5, "Don't stop the music"))
    thePursuit.add(new Track(6, "Love ain't gonna let you down"))
    thePursuit.add(new Track(7, "Mixtape"))
    thePursuit.add(new Track(8, "I think, I love"))
    thePursuit.add(new Track(9, "We run things"))
    thePursuit.add(new Track(10, "Not while I am around"))
    thePursuit.add(new Track(11, "Music is through"))
    thePursuit.add(new Track(12, "Grand Torino"))
    thePursuit.add(new Track(13, "Grace is gone"))

    thePursuit.publishDate = DateTime.now.minusYears(2)

    albums.add(thePursuit)
  }

  protected def assertSingleGruxAlbum(query: Query): Unit = {
    val result = operations.find(query, classOf[Album], COLLECTION_NAME)
    result should not be null
    result.size shouldEqual 1

    assertSingleGruxAlbum(result.get(0))
  }

  protected def assertSingleGruxAlbum(album: Album): Unit = {
    album should not be null
    album.id shouldEqual bigWhiskey.id
    album.title shouldEqual bigWhiskey.title
    album.artist shouldEqual bigWhiskey.artist
    album.tracks.size shouldEqual 13
  }

  protected def assertSinglePursuitAlbum(query: Query): Unit = {
    val result = operations.find(query, classOf[Album], COLLECTION_NAME)
    result should not be null
    result.size shouldEqual 1

    assertSinglePursuitAlbum(result.get(0))
  }
  protected def assertSinglePursuitAlbum(album: Album): Unit = {
    album should not be null
    album.id shouldEqual thePursuit.id
    album.title shouldEqual thePursuit.title
    album.artist shouldEqual thePursuit.artist
    album.tracks.size shouldEqual 14
  }

  protected def assertBothAlbums(albums: util.List[Album]): Unit = {
    albums should not be null
    albums.size shouldEqual 2

    albums.asScala.foreach { album =>
      if (album.id.equals(bigWhiskey.id)) {
        assertSingleGruxAlbum(album)
      } else if (album.id.equals(thePursuit.id)) {
        assertSinglePursuitAlbum(album)
      } else {
        fail("Album is neither Grux or Pursuit!")
      }
    }
  }
}
