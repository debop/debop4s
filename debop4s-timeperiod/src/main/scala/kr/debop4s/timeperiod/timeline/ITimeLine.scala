package kr.debop4s.timeperiod.timeline

import kr.debop4s.timeperiod.{ITimePeriodCollection, ITimePeriodMapper, ITimePeriod, ITimePeriodContainer}

/**
 * kr.debop4s.timeperiod.timeline.ITimeLine
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 31. 오후 8:16
 */
trait ITimeLine extends Serializable {

  def getPeriod: ITimePeriodContainer

  def getLimits: ITimePeriod

  def getPeriodMapper: ITimePeriodMapper

  def combinePeriods: ITimePeriodCollection

  def intersectPeriods: ITimePeriodCollection

  def calculateGaps: ITimePeriodCollection
}