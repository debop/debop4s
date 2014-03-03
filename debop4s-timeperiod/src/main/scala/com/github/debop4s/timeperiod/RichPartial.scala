package com.github.debop4s.timeperiod

import org.joda.time.{ReadablePeriod, Partial}

/**
 * com.github.time.RichPartial
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 6. 오후 8:52
 */
class RichPartial(val self: Partial) extends AnyVal {

  def formatter = self.getFormatter

  def -(period: ReadablePeriod) = self.minus(period)

  def -(builder: DurationBuilder) = self.minus(builder.underlying)

  def +(period: ReadablePeriod) = self.plus(period)

  def +(builder: DurationBuilder) = self.plus(builder.underlying)

}
