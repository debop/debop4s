package debop4s.mongo.datetime

import debop4s.mongo.config.MongoConfigBase
import org.springframework.context.annotation.{ComponentScan, Configuration}
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@Configuration
@ComponentScan(basePackages = Array("kr.hconnect.mongo.datetime"))
@EnableMongoRepositories(basePackageClasses = Array(classOf[TimeseriesRepository]))
class TimeseriesConfiguration extends MongoConfigBase {

  override def getDatabaseName: String = "debop4s-mongo"

  override def getMappingBasePackage: String =
    classOf[Timeseries].getPackage.getName

}
