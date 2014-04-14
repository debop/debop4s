package debop4s.core.time

import java.util.Locale
import org.joda.time._
import org.joda.time.field.AbstractReadableInstantFieldProperty

/**
 * com.github.time.RichAbstractReadableInstantFieldProperty
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 5. 오후 11:42
 */
class RichAbstractReadableInstantFieldProperty(val self: AbstractReadableInstantFieldProperty) extends AnyVal {

    def shortText: String = self.getAsShortText

    def asShortText: String = self.getAsShortText

    def shortText(locale: Locale): String = self.getAsShortText(locale)

    def asShortText(locale: Locale): String = self.getAsShortText(locale)

    def asString: String = self.getAsString

    def text: String = self.getAsText

    def asText: String = self.getAsText

    def text(locale: Locale): String = self.getAsText(locale)

    def asText(locale: Locale): String = self.getAsText(locale)

    def durationField: DurationField = self.getDurationField

    def field: DateTimeField = self.getField

    def fieldType: DateTimeFieldType = self.getFieldType

    def leapAmount: Int = self.getLeapAmount

    def leapDurationField: DurationField = self.getLeapDurationField

    def maximumValue: Int = self.getMaximumValue

    def maxValue: Int = self.getMaximumValue

    def maximumValueOverall: Int = self.getMaximumValueOverall

    def maxValueOverall: Int = self.getMaximumValueOverall

    def minimumValue: Int = self.getMinimumValue

    def minValue: Int = self.getMinimumValue

    def minimumValueOverall: Int = self.getMinimumValueOverall

    def minValueOverall: Int = self.getMinimumValueOverall

    def name: String = self.getName

    def rangeDurationField: DurationField = self.getRangeDurationField

    def interval: Interval = self.toInterval
}
