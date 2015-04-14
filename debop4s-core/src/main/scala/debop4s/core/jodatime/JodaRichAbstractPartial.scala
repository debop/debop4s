package debop4s.core.jodatime

import org.joda.time.base.AbstractPartial
import org.joda.time.{DateTimeField, DateTimeFieldType}

class JodaRichAbstractPartial(val self: AbstractPartial) extends AnyVal with Ordered[AbstractPartial] {

  def field(idx: Int): DateTimeField = self.getField(idx)

  def fields: Array[DateTimeField] = self.getFields

  def fieldTypes: Array[DateTimeFieldType] = self.getFieldTypes

  def values: Array[Int] = self.getValues

  override def compare(that: AbstractPartial): Int = self.compareTo(that)
}
