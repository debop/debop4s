package debop4s.mongo.music

import java.util

import debop4s.mongo.AbstractMongoDocument
import org.joda.time.DateTime

case class Track(var number: Int, var name: String, var rating: Stars = Stars.ZERO) extends AbstractMongoDocument

case class Stars(value: Int)

case object Stars {
  val ZERO = new Stars(0)
  val ONE = new Stars(1)
  val TWO = new Stars(2)
  val THREE = new Stars(3)
  val FOUR = new Stars(4)
  val FIVE = new Stars(5)
  val ALL = Set(ZERO, ONE, TWO, THREE, FOUR, FIVE)
}

@SerialVersionUID(158798390838794475L)
class Album extends AbstractMongoDocument {

  def this(title: String, artist: String) {
    this()
    this.title = title
    this.artist = artist
  }

  var title: String = _
  var artist: String = _
  var publishDate: DateTime = _
  val tracks = new util.ArrayList[Track]()

  def add(track: Track): Unit = {
    this.tracks add track
  }
}
