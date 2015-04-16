package debop4s.mongo.datetime

import debop4s.core.conversions.jodatime._
import debop4s.core.{TimestampZoneText, ToStringHelper}
import debop4s.mongo.AbstractMongoDocument
import org.joda.time.DateTime
import org.springframework.data.mongodb.core.mapping.Document

@Document
class Timeseries extends AbstractMongoDocument {

  def this(datetime: DateTime, score: Long) = {
    this()
    this.time = datetime.asTimestampZoneText
    this.score = score
  }

  var time: TimestampZoneText = _
  var score: Long = _

  override protected def buildStringHelper: ToStringHelper =
    super.buildStringHelper
    .add("time", time)
    .add("score", score)
}
