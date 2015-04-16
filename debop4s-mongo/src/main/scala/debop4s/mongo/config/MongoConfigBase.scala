package debop4s.mongo.config

import com.mongodb._
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.config.AbstractMongoConfiguration

/**
 * MongoDB 사용을 위한 기본 환경설정 정보입니다.
 * @author Sunghyouk Bae
 */
@Configuration
abstract class MongoConfigBase extends AbstractMongoConfiguration {

  /**
   * MongoClient 를 제공합니다.
   *
   * @return { @link com.mongodb.MongoClient} 인스턴스
   * @throws Exception 접속 실패 시
   */
  override def mongo: Mongo =
    new MongoClient(ServerAddress.defaultHost(), mongoOptions)

  /**
   * MongoDB 연결을 위한 옵션을 설정합니다.
   * 특히 WriteConcern 은 성능 및 Validation에 큰 영항을 미치므로 꼭 테스트 후 변경하시기 바랍니다.
   */
  def mongoOptions: MongoClientOptions =
    MongoClientOptions.builder
    .connectionsPerHost(100)
    .threadsAllowedToBlockForConnectionMultiplier(32)
    .socketKeepAlive(true)
    .writeConcern(getWriteConcern)
    .build

  protected def getWriteConcern = WriteConcern.SAFE

}
