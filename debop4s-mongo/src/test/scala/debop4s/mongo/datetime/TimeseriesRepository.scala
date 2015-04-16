package debop4s.mongo.datetime

import java.util

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.transaction.annotation.Transactional

@Transactional
trait TimeseriesRepository extends MongoRepository[Timeseries, String] {

  def save(ts: Timeseries): Timeseries

  def findByTimeTimetext(timetext: String): util.List[Timeseries]

}
