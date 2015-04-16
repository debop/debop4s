package debop4s.mongo.music

import debop4s.mongo.config.MongoConfigBase
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories

@Configuration
@EnableMongoRepositories(basePackageClasses = Array(classOf[AlbumRepository]))
class MusicMongoConfiguration extends MongoConfigBase {

  override def getDatabaseName: String = "debop4s-mongo"

  override def getMappingBasePackage: String =
    classOf[Album].getPackage.getName
}

