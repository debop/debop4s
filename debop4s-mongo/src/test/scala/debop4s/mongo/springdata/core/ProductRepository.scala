package debop4s.mongo.springdata.core

import java.util

import debop4s.mongo.springdata.model
import org.springframework.data.domain.{Page, Pageable}
import org.springframework.data.mongodb.repository.{MongoRepository, Query}
import org.springframework.stereotype.Repository

/**
 * ProductRepository
 * @author sunghyouk.bae@gmail.com
 */
@Repository
trait ProductRepository extends MongoRepository[model.Product, String] {

  def findByDescriptionContaining(description: String, pageable: Pageable): Page[model.Product]

  // NOTE: spring-data-mongodb 1.6.0.RELEASE 부터 제공되지 않습니다.
  @Query("{?0 : ?1}")
  def findByAttributes(key: String, value: String): util.List[model.Product]

}
