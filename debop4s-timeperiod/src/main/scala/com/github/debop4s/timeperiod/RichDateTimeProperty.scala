package com.github.debop4s.timeperiod

import java.util.Locale
import org.joda.time.DateTime

/**
 * com.github.time.RichDateTimeProperty
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 6. 오후 8:27
 */
class RichDateTimeProperty(val self: DateTime.Property) extends AnyVal {

    def dateTime: DateTime = self.getDateTime

    def roundCeiling: DateTime = self.roundCeilingCopy()

    def roundFloor: DateTime = self.roundFloorCopy()

    def roundDown: DateTime = self.roundFloorCopy()

    def roundUp: DateTime = self.roundCeilingCopy()

    def round: DateTime = self.roundHalfEvenCopy()

    def apply(value: Int): DateTime = self.setCopy(value)

    def apply(text: String): DateTime = self.setCopy(text)

    def apply(text: String, locale: Locale): DateTime = self.setCopy(text, locale)
}
