package com.github.debop4s.timeperiod

import org.joda.time.{Chronology, ReadablePartial}

/**
 * com.github.time.RichReadablePartial
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 6. 오후 9:13
 */
class RichReadablePartial(val self: ReadablePartial) extends AnyVal {

  def chronology: Chronology = self.getChronology

  def value(index: Int): Int = self.getValue(index)
}
