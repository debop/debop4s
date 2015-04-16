package debop4s.rediscala.set

import java.util
import java.util.{HashSet => JHashSet, List => JList, Set => JSet}

import debop4s.rediscala.client.RedisSyncClient
import debop4s.rediscala.{MemberRank, MemberRankScore, MemberScore}
import org.springframework.beans.factory.annotation.Autowired

import scala.annotation.varargs
import scala.collection.JavaConverters._

/**
 * Redis Sorted Set 에 대한 처리를 동기 방식으로 수행하는 클래스입니다.
 *
 * @author debop created at 2014. 5. 2.
 */
@deprecated(message = "동기방식은 삭제할 것임", since = "2.0.0")
abstract class AbstractSyncRedisZSet {

  @Autowired val redis: RedisSyncClient = null

  /**
   * 해당 키의 Sorted set의 크기를 구합니다.
   * @param key Sorted set의 키
   * @return Sorted set의 크기
   */
  def size(key: String): Long = redis.zcard(key)

  /**
   * Sorted set의 member의 값을 증가시킵니다. member가 정의되어 있지 않다면 0 이라 간주하고 score 값으로 설정한다.
   * @param key Sorted set key
   * @param score score for increment
   * @param member member name
   * @return  증가된 결과 값
   */
  def incrBy(key: String, score: Double, member: String): Double =
    redis.zincrBy(key, score, member)

  /**
   * Sorted set의 member 들의 값을 증가 시킵니다.
   * @param key Sorted set key
   * @param memberScores  증가할 member 와 score
   * @return  증가된 값
   */
  @varargs
  def incrByAll(key: String, memberScores: MemberScore*): JList[MemberScore] = {
    memberScores
    .map { ms =>
      MemberScore(ms.member, incrBy(key, ms.score, ms.member))
    }.toList
    .asJava
  }


  /**
   * Sorted set의 member 들의 값을 증가 시킵니다.
   * @param key Sorted set key
   * @param memberScores  증가할 member 와 score
   * @return  증가된 값
   */
  def incrByAll(key: String, memberScores: JList[MemberScore]): JList[MemberScore] =
    incrByAll(key, memberScores.asScala.toSeq: _*)

  /**
   * Sorted set의 member 의 score 값을 설정합니다.
   * @param key  Sorted set key
   * @param score  score to set
   * @param member member to set
   * @return result score
   */
  def set(key: String, score: Double, member: String): Long =
    redis.zadd(key, score, member)

  /**
   * Sorted set 에 복수개의 member의 score 값을 설정합니다.
   * @param key  sorted set key name
   * @param memberScores  member and score
   * @return
   */
  @varargs
  def setAll(key: String, memberScores: MemberScore*): Long = {
    redis.zadd(key, memberScores: _*)
  }

  def setAll(key: String, memberScores: JList[MemberScore]): Long =
    redis.zadd(key, memberScores.asScala.toSeq: _*)

  def score(key: String, member: String): MemberScore =
    MemberScore(member, redis.zscore(key, member).getOrElse(0D))

  @varargs
  def scoreAll(key: String, members: String*): JList[MemberScore] = {
    members
    .map { m =>
      MemberScore(m, redis.zscore(key, m).getOrElse(0D))
    }.toList
    .sortBy(_.member)
    .asJava
  }


  def scoreAll(key: String, members: JSet[String]): JList[MemberScore] =
    scoreAll(key, members.asScala.toSeq: _*)

  def rank(key: String, member: String): MemberRank =
    MemberRank(member, redis.zrank(key, member).getOrElse(Long.MaxValue))


  def revRank(key: String, member: String): MemberRank =
    MemberRank(member, redis.zrevrank(key, member).getOrElse(Long.MaxValue))

  def rankWithScore(key: String, member: String): MemberRankScore = {
    val rank = redis.zrank(key, member).getOrElse(Long.MaxValue)
    val score = redis.zscore(key, member).getOrElse(0D)
    MemberRankScore(member, rank, score)
  }

  def revRankWithScore(key: String, member: String): MemberRankScore = {
    val rank = redis.zrevrank(key, member).getOrElse(Long.MaxValue)
    val score = redis.zscore(key, member).getOrElse(0D)
    MemberRankScore(member, rank, score)
  }

  @varargs
  def rankAll(key: String, members: String*): JList[MemberRank] = {
    members
    .map { m =>
      rank(key, m)
    }.toList
    .sortBy(_.rank)
    .asJava
  }

  def rankAll(key: String, members: JSet[String]): JList[MemberRank] = {
    rankAll(key, members.asScala.toSeq: _*)
  }

  @varargs
  def revRankAll(key: String, members: String*): JList[MemberRank] = {
    members.map { m =>
      revRank(key, m)
    }.toList
    .sortBy(_.rank)
    .asJava
  }

  def revRankAll(key: String, members: JSet[String]): JList[MemberRank] = {
    revRankAll(key, members.asScala.toSeq: _*)
  }

  @varargs
  def rankAllWithScores(key: String, members: String*): JList[MemberRankScore] = {
    members.map { m =>
      rankWithScore(key, m)
    }.toList
    .sortBy(_.rank)
    .asJava
  }

  def rankAllWithScores(key: String, members: JSet[String]): JList[MemberRankScore] = {
    rankAllWithScores(key, members.asScala.toSeq: _*)
  }

  @varargs
  def revRankAllWithScores(key: String, members: String*): JList[MemberRankScore] = {
    members
    .map { m =>
      revRankWithScore(key, m)
    }.toList
    .sortBy(_.rank)
    .asJava
  }

  def revRankAllWithScores(key: String, members: JSet[String]): JList[MemberRankScore] =
    revRankAllWithScores(key, members.asScala.toSeq: _*)

  @varargs
  def rankAllInGroup(key: String, members: String*): JList[MemberRank] = {
    val mrs = rankAll(key, members: _*)

    val results = new util.ArrayList[MemberRank](mrs.size())
    var rank = 0
    while (rank < mrs.size) {
      results add MemberRank(mrs.get(rank).member, rank)
      rank += 1
    }
    results
  }

  def rankAllInGroup(key: String, members: JSet[String]): JList[MemberRank] = {
    rankAllInGroup(key, members.asScala.toSeq: _*)
  }

  @varargs
  def revRankAllInGroup(key: String, members: String*): JList[MemberRank] = {
    val mrs = revRankAll(key, members: _*)

    val results = new util.ArrayList[MemberRank](mrs.size())
    var rank = 0
    while (rank < mrs.size) {
      results add MemberRank(mrs.get(rank).member, rank)
      rank += 1
    }
    results
  }

  def revRankAllInGroup(key: String, members: JSet[String]): JList[MemberRank] = {
    revRankAllInGroup(key, members.asScala.toSeq: _*)
  }

  @varargs
  def rankAllInGroupWithScores(key: String, members: String*): JList[MemberRankScore] = {
    val mrs = rankAllWithScores(key, members: _*)

    val results = new util.ArrayList[MemberRankScore](mrs.size())
    var rank = 0
    while (rank < mrs.size) {
      results add MemberRankScore(mrs.get(rank).member, rank, mrs.get(rank).score)
      rank += 1
    }
    results
  }

  def rankAllInGroupWithScores(key: String, members: JSet[String]): JList[MemberRankScore] = {
    rankAllInGroupWithScores(key, members.asScala.toSeq: _*)
  }

  @varargs
  def revRankAllInGroupWithScores(key: String, members: String*): JList[MemberRankScore] = {
    val mrs = revRankAllWithScores(key, members: _*)

    val results = new util.ArrayList[MemberRankScore](mrs.size())
    var rank = 0
    while (rank < mrs.size) {
      results add MemberRankScore(mrs.get(rank).member, rank, mrs.get(rank).score)
      rank += 1
    }
    results
  }

  def revRankAllInGroupWithScores(key: String, members: JSet[String]): JList[MemberRankScore] = {
    revRankAllInGroupWithScores(key, members.asScala.toSeq: _*)
  }

  def rangeWithScores(key: String, start: Long = 0, end: Long = -1): JList[MemberScore] =
    redis.zrangeWithScores(key, start, end).asJava


  def revRangeWithScores(key: String, start: Long = 0, end: Long = -1): JList[MemberScore] =
    redis.zrevrangeWithScores(key, start, end).asJava


}
