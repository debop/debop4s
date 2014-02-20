package com.github.debop4s.timeperiod

import java.util.{Calendar, Date}
import org.joda.time._

object StaticLocalDate extends StaticLocalDate

trait StaticLocalDate {
    type Property = LocalDate.Property

    def fromCalendarFields(calendar: Calendar) = LocalDate.fromCalendarFields(calendar)

    def fromDateFields(date: Date) = LocalDate.fromDateFields(date)

    def now = new LocalDate

    def today = new LocalDate

    def nextDay = now + 1.day

    def tomorrow = now + 1.day

    def nextWeek = now + 1.week

    def nextMonth = now + 1.month

    def nextYear = now + 1.year

    def lastDay = now - 1.day

    def yesterday = now - 1.day

    def lastWeek = now - 1.week

    def lastMonth = now - 1.month

    def lastYear = now - 1.year
}
