package debop4s.redis.base


case class MemberScore(member: String, score: Double)

case class MemberRank(member: String, rank: Long)

case class MemberRankScore(member: String,
                           rank: Long = 0,
                           score: Double = 0)

object MemberRankScore {

    def apply(member: String): MemberRankScore = {
        new MemberRankScore(member, 0, 0)
    }
}
