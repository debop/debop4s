package debop4s.rediscala

import java.lang.{Long => JLong}

/**
 * Redis Sorted Set의 Member 와 Score 를 표현합니다.
 * @param member Sorted Set의 member
 * @param score  Sorted Set의 score
 */
case class MemberScore(member: String, score: Double = 0D) {
  def tuple: (String, Double) = (member, score)
}

/**
 * Redis Sorted Set의 Member 와 Score 를 표현합니다.
 * @param member Sorted Set의 member
 * @param rank  member의 rank
 */
case class MemberRank(member: String, rank: Long = Long.MaxValue) {
  def tuple = (member, rank)
}

/**
 * Redis Sorted Set의 Member 와 Score 를 표현합니다.
 * @param member Sorted Set의 member
 * @param score  Sorted Set의 score
 * @param rank  member의 rank
 */
case class MemberRankScore(member: String,
                           rank: Long = Long.MaxValue,
                           score: Double = 0D) {

  def tuple = (member, rank, score)
}

