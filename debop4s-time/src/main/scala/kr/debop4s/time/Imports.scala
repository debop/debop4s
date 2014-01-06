package kr.debop4s.time

/**
 * kr.debop4s.time.Imports
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 6. 오후 9:30
 */
object Imports extends Imports

object TypeImports extends TypeImports

object StaticForwarderImports extends StaticForwarderImports

trait Imports extends TypeImports with StaticForwarderImports

trait TypeImports {
    type Chronology = org.joda.time.Chronology
    type DateTime = org.joda.time.DateTime
    type DateTimeFormat = org.joda.time.format.DateTimeFormat
    type DateTimeZone = org.joda.time.DateTimeZone
    type Duration = org.joda.time.Duration
    type Interval = org.joda.time.Interval
    type LocalDate = org.joda.time.LocalDate
    type LocalDateTime = org.joda.time.LocalDateTime
    type LocalTime = org.joda.time.LocalTime
    type Period = org.joda.time.Period
    type Partial = org.joda.time.Partial
}

trait StaticForwarderImports {
    val DateTime = kr.debop4s.time.StaticDateTime
    val DateTimeFormat = kr.debop4s.time.StaticDateTimeFormat
    val ISODateTimeFormat = kr.debop4s.time.StaticISODateTimeFormat
    val DateTimeZone = kr.debop4s.time.StaticDateTimeZone
    val Duration = kr.debop4s.time.StaticDuration
    val Interval = kr.debop4s.time.StaticInterval
    val LocalDate = kr.debop4s.time.StaticLocalDate
    val LocalDateTime = kr.debop4s.time.StaticLocalDateTime
    val LocalTime = kr.debop4s.time.StaticLocalTime
    val Period = kr.debop4s.time.StaticPeriod
    val Partial = kr.debop4s.time.StaticPartial
}
