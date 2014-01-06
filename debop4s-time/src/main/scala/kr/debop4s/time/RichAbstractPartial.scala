package kr.debop4s.time

import org.joda.time.base.AbstractPartial

/**
 * kr.debop4s.time.RichAbstractPartial
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 5. 오후 11:39
 */
class RichAbstractPartial(val self: AbstractPartial) extends AnyVal with Ordered[AbstractPartial] {

    def fields = self.getFields
    def fieldTypes = self.getFieldTypes
    def values = self.getValues

    override def compare(that: AbstractPartial): Int = self.compareTo(that)
}
