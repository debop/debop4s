package debop4s.mongo.springdata

import java.util

import debop4s.mongo.config.MongoConfigBase
import debop4s.mongo.springdata.core.CustomerRepository
import debop4s.mongo.springdata.model.Customer
import debop4s.mongo.springdata.order.OrderRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.{ComponentScan, Configuration}
import org.springframework.core.convert.converter.Converter
import org.springframework.data.mongodb.core.convert.CustomConversions
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

/**
 * ApplicationConfiguration
 * @author sunghyouk.bae@gmail.com 14. 10. 19.
 */
@Configuration
@ComponentScan(basePackages = Array("debop4s.mongo.springdata"))
@EnableMongoRepositories(basePackageClasses = Array(classOf[CustomerRepository],
  classOf[OrderRepository]))
class ApplicationConfiguration extends MongoConfigBase {

  override def getDatabaseName: String = "debop4s-mongo"

  override def getMappingBasePackage: String =
    classOf[Customer].getPackage.getName

  @Autowired val converters: util.List[Converter[_, _]] = null

  override def customConversions(): CustomConversions =
    new CustomConversions(converters)
}
