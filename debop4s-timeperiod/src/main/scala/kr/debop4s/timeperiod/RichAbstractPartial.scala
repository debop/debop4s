package kr.debop4s.timeperiod

import org.joda.time.base.AbstractPartial
import org.joda.time.{DateTimeFieldType, DateTimeField}

/**
 * kr.debop4s.time.RichAbstractPartial
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 5. 오후 11:39
 */
class RichAbstractPartial(val self: AbstractPartial) extends AnyVal with Ordered[AbstractPartial] {

    def field(idx: Int): DateTimeField = self.getField(idx)

    def fields: Array[DateTimeField] = self.getFields

    def fieldTypes: Array[DateTimeFieldType] = self.getFieldTypes

    def values: Array[Int] = self.getValues

    override def compare(that: AbstractPartial): Int = self.compareTo(that)
}
