package com.github.debop4s.redis.base


case class MemberScore(member: String, score: Double)

case class MemberRank(member: String, rank: Long)

case class MemberRankScore(member: String,
                           rank: Long,
                           score: Double)
