package debop4s.timeperiod

object PeriodFlag {

  val None: Set[Flag.Value] = Set(Flag.None)
  val Year: Set[Flag.Value] = Set(Flag.Year)
  val Halfyear: Set[Flag.Value] = Set(Flag.Halfyear)
  val Quarter: Set[Flag.Value] = Set(Flag.Quarter)
  val Month: Set[Flag.Value] = Set(Flag.Month)
  val Week: Set[Flag.Value] = Set(Flag.Week)
  val Day: Set[Flag.Value] = Set(Flag.Day)
  val Hour: Set[Flag.Value] = Set(Flag.Hour)

  val YearMonth: Set[Flag.Value] = Set(Flag.Year, Flag.Month)
  val YearMonthDay: Set[Flag.Value] = Set(Flag.Year, Flag.Month, Flag.Day)
  val YearMonthDayHour: Set[Flag.Value] = Set(Flag.Year, Flag.Month, Flag.Day, Flag.Hour)

  val YearQuarter: Set[Flag.Value] = Set(Flag.Year, Flag.Quarter)
  val YearQuarterMonth: Set[Flag.Value] = Set(Flag.Year, Flag.Quarter, Flag.Month)
  val YearQuarterMonthDay: Set[Flag.Value] = Set(Flag.Year, Flag.Quarter, Flag.Month, Flag.Day)

  val YearWeek: Set[Flag.Value] = Set(Flag.Year, Flag.Week)
  val YearWeekDay: Set[Flag.Value] = Set(Flag.Year, Flag.Week, Flag.Day)
  val YearWeekDayHour: Set[Flag.Value] = Set(Flag.Year, Flag.Week, Flag.Day, Flag.Hour)

  val MonthDay = Set(Flag.Month, Flag.Day)
  val MonthDayHour = Set(Flag.Month, Flag.Day, Flag.Hour)


  object Flag extends Enumeration {
    type Flag = Value

    val None = Value(0x00)
    val Year = Value(0x01)
    val Halfyear = Value(0x02)
    val Quarter = Value(0x04)
    val Month = Value(0x08)
    val Week = Value(0x10)
    val Day = Value(0x20)
    val Hour = Value(0x40)
  }

}
