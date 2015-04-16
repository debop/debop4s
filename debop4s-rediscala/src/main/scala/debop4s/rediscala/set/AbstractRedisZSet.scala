package debop4s.rediscala.set

import java.util
import java.util.{List => JList, Map => JMap, Set => JSet}

import com.google.common.collect.Lists
import debop4s.core.concurrent.{Asyncs, _}
import debop4s.rediscala.{MemberRank, MemberRankScore, MemberScore}
import org.springframework.beans.factory.annotation.Autowired
import redis.RedisClient

import scala.annotation.varargs
import scala.async.Async._
import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Redis Sorted Set 저장소에 대한 특화 작업을 제공합니다.
 *
 * @author Sunghyouk Bae
 */
abstract class AbstractRedisZSet {

  @Autowired val redis: RedisClient = null

  def size(key: String): Future[Long] = redis.zcard(key)

  def incrBy(key: String, score: Double, member: String): Future[Double] = {
    redis.zincrby(key, score, member)
  }

  @varargs
  def incrByAll(key: String, memberScores: MemberScore*): JList[MemberScore] = {
    val results = memberScores.map { ms =>
      redis.zincrby(key, ms.score, ms.member) map { s => MemberScore(ms.member, s) }
    }
    // Asyncs.resultAll(results.seq).toList.asJava
    results.seq.awaitAll.toList.asJava
  }

  def incrByAll(key: String, memberScores: JList[MemberScore]): JList[MemberScore] =
    incrByAll(key, memberScores.asScala: _*)

  def set(key: String, score: Double, member: String): Future[Long] = {
    redis.zadd(key, (score, member))
  }

  @varargs
  def setAll(key: String, memberScores: MemberScore*): Future[Long] = {
    redis.zadd(key, memberScores.map(ms => (ms.score, ms.member)).toSeq: _*)
  }

  def setAll(key: String, memberScores: JList[MemberScore]): Future[Long] = {
    redis.zadd(key, memberScores.asScala.map(ms => (ms.score, ms.member)).toSeq: _*)
  }

  def score(key: String, member: String): Future[MemberScore] = {
    redis.zscore(key, member).map(x => MemberScore(member, x.getOrElse(0D)))
  }

  @varargs
  def scoreAll(key: String, members: String*): JList[MemberScore] = {
    val results = members.map { m =>
      redis.zscore(key, m).map(s => MemberScore(m, s.getOrElse(0D)))
    }
    Asyncs.resultAll(results.seq).toList.asJava
  }

  def scoreAll(key: String, members: JSet[String]): JList[MemberScore] = {
    scoreAll(key, members.asScala.toSeq: _*)
  }

  def rank(key: String, member: String): Future[MemberRank] = {
    redis.zrank(key, member).map(r => MemberRank(member, r.getOrElse(Long.MaxValue)))
  }

  def revRank(key: String, member: String): Future[MemberRank] = {
    redis.zrevrank(key, member).map(r => MemberRank(member, r.getOrElse(Long.MaxValue)))
  }

  def rankWithScore(key: String, member: String): Future[MemberRankScore] = {
    async {
      val rank = redis.zrank(key, member)
      val score = redis.zscore(key, member)
      MemberRankScore(member, await(rank).getOrElse(Long.MaxValue), await(score).getOrElse(0D))
    }
  }

  def revRankWithScore(key: String, member: String): Future[MemberRankScore] = {
    async {
      val rank = redis.zrevrank(key, member)
      val score = redis.zscore(key, member)
      MemberRankScore(member, await(rank).getOrElse(Long.MaxValue), await(score).getOrElse(0D))
    }
  }

  @varargs
  def rankAll(key: String, members: String*): JList[MemberRank] = {
    val results = members.map { m =>
      redis.zrank(key, m) map { r => MemberRank(m, r.getOrElse(Long.MaxValue)) }
    }
    Asyncs.resultAll(results.seq).toList.sortBy(_.rank).asJava
  }

  def rankAll(key: String, members: JSet[String]): JList[MemberRank] = {
    rankAll(key, members.asScala.toSeq: _*)
  }

  @varargs
  def revRankAll(key: String, members: String*): JList[MemberRank] = {
    val results = members.map { m =>
      redis.zrevrank(key, m).map { r => MemberRank(m, r.getOrElse(Long.MaxValue)) }
    }
    Asyncs.resultAll(results.seq).toList.sortBy(_.rank).asJava
  }

  def revRankAll(key: String, members: JSet[String]): JList[MemberRank] = {
    revRankAll(key, members.asScala.toSeq: _*)
  }

  @varargs
  def rankAllWithScores(key: String, members: String*): JList[MemberRankScore] = {
    val results = members.map { m =>
      rankWithScore(key, m)
    }
    Asyncs.resultAll(results.seq).toList.sortBy(_.rank).asJava
  }
  def rankAllWithScores(key: String, members: JSet[String]): JList[MemberRankScore] = {
    rankAllWithScores(key, members.asScala.toSeq: _*)
  }

  @varargs
  def revRankAllWithScores(key: String, members: String*): JList[MemberRankScore] = {
    val results = members.map { m =>
      revRankWithScore(key, m)
    }
    Asyncs.resultAll(results.seq).toList.sortBy(_.rank).asJava
  }

  def revRankAllWithScores(key: String, members: JSet[String]): JList[MemberRankScore] = {
    revRankAllWithScores(key, members.asScala.toSeq: _*)
  }

  @varargs
  def rankAllInGroup(key: String, members: String*): JList[MemberRank] = {
    val ranks = rankAll(key, members: _*)

    val results = Lists.newArrayListWithCapacity[MemberRank](ranks.size)
    var i = 0
    while (i < ranks.size()) {
      results add MemberRank(ranks.get(i).member, i.toLong)
      i += 1
    }
    results
  }
  def rankAllInGroup(key: String, members: JSet[String]): JList[MemberRank] = {
    rankAllInGroup(key, members.asScala.toSeq: _*)
  }

  @varargs
  def revRankAllInGroup(key: String, members: String*): JList[MemberRank] = {
    val ranks = revRankAll(key, members: _*)
    val results = Lists.newArrayListWithCapacity[MemberRank](ranks.size())

    var i = 0
    while (i < ranks.size()) {
      results.add(MemberRank(ranks.get(i).member, i.toLong))
      i += 1
    }
    results
  }
  def revRankAllInGroup(key: String, members: JSet[String]): JList[MemberRank] = {
    revRankAllInGroup(key, members.asScala.toSeq: _*)
  }

  @varargs
  def rankAllInGroupWithScores(key: String, members: String*): JList[MemberRankScore] = {
    val mrss = rankAllWithScores(key, members: _*)

    val results = new util.ArrayList[MemberRankScore](members.length)
    var i = 0
    while (i < mrss.size()) {
      val mrs = mrss.get(i)
      results add MemberRankScore(mrs.member, i, mrs.score)
      i += 1
    }
    results
  }

  def rankAllInGroupWithScores(key: String, members: JSet[String]): JList[MemberRankScore] = {
    rankAllInGroupWithScores(key, members.asScala.toSeq: _*)
  }

  @varargs
  def revRankAllInGroupWithScores(key: String, members: String*): JList[MemberRankScore] = {
    val mrss = revRankAllWithScores(key, members: _*)

    val results = new util.ArrayList[MemberRankScore](members.length)
    var i = 0
    while (i < mrss.size()) {
      val mrs = mrss.get(i)
      results add MemberRankScore(mrs.member, i, mrs.score)
      i += 1
    }
    results
  }

  def revRankAllInGroupWithScores(key: String, members: JSet[String]): JList[MemberRankScore] = {
    revRankAllInGroupWithScores(key, members.asScala.toSeq: _*)
  }

  def rangeWithScores(key: String, start: Long, end: Long): Future[JList[MemberScore]] = {
    redis.zrangeWithscores[String](key, start, end) map { ms =>
      val results = new util.ArrayList[MemberScore](ms.length)
      val iter = ms.iterator
      while (iter.hasNext) {
        val (m, s) = iter.next()
        results add MemberScore(m, s)
      }
      results
    }
  }

  def revRangeWithScores(key: String, start: Long, end: Long): Future[JList[MemberScore]] = {
    redis.zrevrangeWithscores[String](key, start, end).map { ms =>
      val results = new util.ArrayList[MemberScore](ms.length)
      val iter = ms.iterator
      while (iter.hasNext) {
        val (m, s) = iter.next()
        results add MemberScore(m, s)
      }
      results
    }
  }
}
