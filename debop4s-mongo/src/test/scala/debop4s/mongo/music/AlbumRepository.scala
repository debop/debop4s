package debop4s.mongo.music

import java.util

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional


@Repository
@Transactional
trait AlbumRepository extends MongoRepository[Album, String] {

  def findByTracksName(name: String): util.List[Album]

  def findByTracksNameLike(name: String): util.List[Album]

  def findByTracksRatingGreaterThan(rating: Stars): util.List[Album]

}