package debop4s.mongo.datetime

import debop4s.mongo.config.MongoConfigBase
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@Configuration
// @ComponentScan(basePackageClasses = Array(classOf[TimeseriesRepository]))
@EnableMongoRepositories(basePackageClasses = Array(classOf[TimeseriesRepository]))
class TimeseriesConfiguration extends MongoConfigBase {

  override def getDatabaseName: String = "debop4s-mongo"

  override def getMappingBasePackage: String =
    classOf[Timeseries].getPackage.getName

}
