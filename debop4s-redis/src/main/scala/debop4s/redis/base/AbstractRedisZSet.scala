package debop4s.redis.base

import debop4s.core.parallels.Asyncs
import org.springframework.beans.factory.annotation.Autowired
import redis.RedisClient
import scala.annotation.varargs
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Redis Sorted Set 저장소에 대한 특화 작업을 제공합니다.
 * @author Sunghyouk Bae
 */
abstract class AbstractRedisZSet {

  @Autowired protected val redis: RedisClient = null

  def size(key: String): Future[Long] = {
    redis.zcard(key)
  }

  def incrby(key: String, score: Double, member: String): Future[Double] = {
    redis.zincrby(key, score, member)
  }

  @varargs
  def incrByAll(key: String, memberScores: MemberScore*): Seq[Future[Double]] = {
    memberScores.map(ms => redis.zincrby(key, ms.score, ms.member))
  }

  def set(key: String, score: Double, member: String): Future[Long] = {
    redis.zadd(key, (score, member))
  }

  @varargs
  def setAll(key: String, memberScores: MemberScore*): Future[Long] = {
    redis.zadd(key, memberScores.map(ms => (ms.score, ms.member)).toSeq: _*)
  }

  def score(key: String, member: String): Future[MemberScore] = {
    redis.zscore(key, member).map(x => MemberScore(member, x.getOrElse(0D)))
  }

  @varargs
  def scoreAll(key: String, members: String*): Seq[Future[MemberScore]] = {
    members.map(m => redis.zscore(key, m).map(s => MemberScore(m, s.getOrElse(0D))))
  }

  def rank(key: String, member: String): Future[MemberRank] = {
    redis.zrank(key, member).map(r => MemberRank(member, r.getOrElse(0L)))
  }

  def revRank(key: String, member: String): Future[MemberRank] = {
    redis.zrevrank(key, member).map(r => MemberRank(member, r.getOrElse(0L)))
  }

  def rankWithScore(key: String, member: String): Future[MemberRankScore] = {
    for {
      rank <- redis.zrank(key, member)
      score <- redis.zscore(key, member)
    } yield {
      MemberRankScore(member, rank.getOrElse(0L), score.getOrElse(0D))
    }
  }

  def revRankWithScore(key: String, member: String): Future[MemberRankScore] = {
    for {
      rank <- redis.zrevrank(key, member)
      score <- redis.zscore(key, member)
    } yield {
      MemberRankScore(member, rank.getOrElse(0L), score.getOrElse(0D))
    }
  }

  @varargs
  def rankAll(key: String, members: String*): Seq[Future[MemberRank]] = {
    members.map { m =>
      redis.zrank(key, m).map { r =>
        MemberRank(m, r.getOrElse(0L))
      }
    }
  }

  @varargs
  def revRankAll(key: String, members: String*): Seq[Future[MemberRank]] = {
    members.map { m =>
      redis.zrevrank(key, m).map { r =>
        MemberRank(m, r.getOrElse(0L))
      }
    }
  }

  @varargs
  def rankAllWithScores(key: String, members: String*): Seq[MemberRankScore] = {
    rankAll(key, members: _*)
    .map { fmr =>
      val mr = Asyncs.result(fmr)
      val fs = redis.zscore(key, mr.member)
      MemberRankScore(mr.member, mr.rank, Asyncs.result(fs).getOrElse(0D))
    }
  }

  @varargs
  def revRankAllWithScores(key: String, members: String*): Seq[MemberRankScore] = {
    revRankAll(key, members: _*)
    .map { fmr =>
      val mr = Asyncs.result(fmr)
      val fs = redis.zscore(key, mr.member)
      MemberRankScore(mr.member, mr.rank, Asyncs.result(fs).getOrElse(0D))
    }
  }

  @varargs
  def rankAllInGroup(key: String, members: String*): Seq[MemberRank] = {
    var rank = 0L

    rankAll(key, members: _*)
    .map { fmr =>
      val mr = Asyncs.result(fmr)
      val elem = MemberRank(mr.member, rank)
      rank += 1
      elem
    }.toSeq
    .sortBy(_.rank)
  }

  @varargs
  def revRankInGroup(key: String, members: String*): Seq[MemberRank] = {
    var rank = 0L

    revRankAll(key, members: _*)
    .map { fmr =>
      val mr = Asyncs.result(fmr)
      val elem = MemberRank(mr.member, rank)
      rank += 1
      elem
    }.toSeq
    .sortBy(_.rank)
  }

  @varargs
  def rankAllInGroupWithScores(key: String, members: String*): Seq[MemberRankScore] = {
    var rank = 0L

    rankAllWithScores(key, members: _*)
    .map { mrs =>
      val elem = MemberRankScore(mrs.member, rank, mrs.score)
      rank += 1
      elem
    }.toSeq
    .sortBy(_.rank)
  }

  @varargs
  def revRankInGroupWithScores(key: String, members: String*): Seq[MemberRankScore] = {
    var rank = 0L

    rankAllWithScores(key, members: _*)
    .map { mrs =>
      val elem = MemberRankScore(mrs.member, rank, mrs.score)
      rank += 1
      elem
    }.toSeq
    .sortBy(_.rank)
  }

  def rangeWithScores(key: String, start: Long, end: Long): Future[Seq[MemberScore]] = {
    redis.zrangeWithscores(key, start, end)
    .map { r =>
      r.map { case (member, score) =>
        MemberScore(member.toString(), score)
      }.toSeq
      .sortBy(_.score)
    }
  }

  def revRangeWithScores(key: String, start: Long, end: Long): Future[Seq[MemberScore]] = {
    redis.zrevrangeWithscores(key, start, end)
    .map { r =>
      r.map { case (member, score) =>
        MemberScore(member.toString(), score)
      }.toSeq
      .sortBy(_.score)
    }
  }
}
