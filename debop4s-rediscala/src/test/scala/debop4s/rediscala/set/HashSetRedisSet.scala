package debop4s.rediscala.set

import org.springframework.stereotype.Component

import scala.collection.mutable


@Component
class HashSetRedisSet extends AbstractRedisSet[mutable.HashSet[String]]
