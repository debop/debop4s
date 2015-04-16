package debop4s.mongo.spring

import debop4s.core.io.FstSerializer
import org.slf4j.LoggerFactory
import org.springframework.cache.Cache
import org.springframework.cache.Cache.ValueWrapper
import org.springframework.cache.support.SimpleValueWrapper
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.{Criteria, Query, Update}

import scala.util.control.NonFatal

object MongoCache {

  def apply(name: String, prefix: String, mongo: MongoTemplate, expiration: Long): MongoCache =
    new MongoCache(name, prefix, mongo, expiration)
}

/**
 * Spring @Cacheable 을 지원하고, MongoDB에 캐시 항목을 관리하는 클래스입니다.
 *
 * @author Sunghyouk Bae
 */
class MongoCache(val name: String,
                 val prefix: String,
                 val mongo: MongoTemplate,
                 val expiration: Long) extends Cache {

  private lazy val log = LoggerFactory.getLogger(getClass)

  private val serializer = new FstSerializer()

  override def getName: String = name
  override def getNativeCache: AnyRef = mongo

  override def get(key: Any): ValueWrapper = {
    log.trace(s"캐시를 로드합니다... key=[$key]")
    var value: ValueWrapper = null

    try {
      val item = mongo.findOne(Query.query(Criteria.where("key").is(key)), classOf[MongoCacheItem], name)
      if (item != null) {
        if (item.expireAt <= 0 || item.expireAt > System.currentTimeMillis())
          value = new SimpleValueWrapper(serializer.deserialize(item.value, classOf[AnyRef]))
        else
          evict(key)

        log.trace(s"캐시 값 조회. key=[$key], value=[$value], expireAt=[${ item.expireAt }]")
      }
    } catch {
      case NonFatal(e) => log.warn(s"캐시 조회에 실패했습니다. key=$key", e)
    }
    value
  }

  // override for Spring 4.0.x
  // override
  def get[T](key: Any, clazz: Class[T]): T = {
    log.trace(s"캐시를 로드합니다... key=[$key]")
    var value: T = null.asInstanceOf[T]

    try {
      val item = mongo.findOne(Query.query(Criteria.where("key").is(key)), classOf[MongoCacheItem], name)
      if (item != null) {
        if (item.expireAt <= 0 || item.expireAt > System.currentTimeMillis())
          value = serializer.deserialize(item.value, clazz)
        else
          evict(key)

        log.trace(s"캐시 값 조회. key=[$key], value=[$value], expireAt=[${ item.expireAt }]")
      }
    } catch {
      case NonFatal(e) => log.warn(s"캐시 조회에 실패했습니다. key=$key", e)
    }
    value
  }
  /**
   * 캐시를 MongoDB에 저장합니다.
   *
   * @param key   캐시 키
   * @param value 캐시 값
   */
  override def put(key: Any, value: Any) {
    log.trace(s"캐시를 저장합니다. key=[$key], value=[$value]")
    try {
      val query: Query = Query.query(Criteria.where("key").is(key))
      val update: Update = Update.update("value", serializer.serialize(value))
      mongo.upsert(query, update, name)
    }
    catch {
      case e: Exception => log.warn(s"캐시 저장에 실패했습니다. key=$key, value=$value", e)
    }
  }

  /**
   * 캐시가 없을 때에만 저장합니다.
   * @param key   캐시 키
   * @param value 캐시 값
   * @return 캐시 값의 `ValueWrapper`
   */
  override def putIfAbsent(key: Any, value: Any): ValueWrapper = {
    val result = get(key)
    if (result == null) {
      put(key, value)
    }
    result
  }

  /**
   * 캐시를 삭제합니다.
   *
   * @param key 캐시 키
   */
  override def evict(key: Any) {
    log.trace(s"캐시를 삭제합니다. key=$key")
    try {
      val query = Query.query(Criteria.where("key").is(key))
      mongo.remove(query, name)
    } catch {
      case NonFatal(e) =>
        log.warn(s"캐시를 삭제하는데 실패했습니다. key=$key", e)
    }
  }
  /**
   * 현 캐시가 관리하는 모든 캐시 항목을 삭제합니다.
   */
  override def clear() {
    log.debug(s"캐시 저장 컬렉션을 삭제합니다... name=$name")
    try {
      mongo.dropCollection(name)
    } catch {
      case NonFatal(e) =>
        log.warn(s"캐시 전체를 삭제하는데 실패했습니다. collection name=$name", e)
    }
  }

}
