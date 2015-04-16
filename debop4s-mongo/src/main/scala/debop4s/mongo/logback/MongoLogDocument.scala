package debop4s.mongo.logback

import debop4s.core.logback.LogDocument

/**
 * Logback 용 로그 정보를 MongoDB 에 저장하기 위한 엔티티입니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 */
@org.springframework.data.mongodb.core.mapping.Document
@SerialVersionUID(2192569271777171508L)
class MongoLogDocument extends LogDocument {

  /** MongoDB Entity의 Identifier */
  @org.springframework.data.annotation.Id
  private var id: java.math.BigInteger = _

}

