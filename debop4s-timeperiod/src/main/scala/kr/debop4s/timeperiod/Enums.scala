package kr.debop4s.timeperiod


object DayOfWeek extends scala.Enumeration {

  type DayOfWeek = Value

  val Monday = Value(1, "Monday")
  val Tuesday = Value(2, "Tuesday")
  val Wednesday = Value(3, "Wednesday")
  val Thursday = Value(4, "Thursday")
  val Friday = Value(5, "Friday")
  val Saturday = Value(6, "Saturday")
  val Sunday = Value(7, "Sunday")
}

object Halfyear extends Enumeration {

  type Halfyear = Value

  val First = Value(1, "First")
  val Second = Value(2, "Second")
}

object IntervalEdge extends Enumeration {

  type IntervalEdge = Value

  val Closed = Value("Closed")
  val Opened = Value("Opened")

}

object Month extends Enumeration {

  type Month = Value

  val January = Value(1, "January")
  val Feburary = Value(2, "Feburary")
  val March = Value(3, "March")
  val April = Value(4, "April")
  val May = Value(5, "May")
  val June = Value(6, "June")
  val July = Value(7, "July")
  val Auguest = Value(8, "Auguest")
  val September = Value(9, "September")
  val October = Value(10, "October")
  val November = Value(11, "November")
  val December = Value(12, "December")
}

object OrderDirection extends Enumeration {
  type OrderDirection = Value

  val ASC = Value("ASC")
  val DESC = Value("DESC")
}

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


object PeriodRelation extends Enumeration {

  type PeriodRelation = Value

  /** 알 수 없음 (두개의 기간([[kr.debop4s.timeperiod.TimePeriod]])이 모두 Anytime 일 경우) */
  val NoRelation = Value("NoRelation")

  /** 현 [[kr.debop4s.timeperiod.TimePeriod]] 이후에 대상 [[kr.debop4s.timeperiod.TimePeriod]]가 있을 때 */
  val After = Value("After")

  /** 현 [[kr.debop4s.timeperiod.TimePeriod]]의 완료 시각이 대상 [[kr.debop4s.timeperiod.TimePeriod]]의 시작 시각과 같습니다. */
  val StartTouching = Value("StartTouching")

  /** 현 [[kr.debop4s.timeperiod.TimePeriod]] 기간 안에 대상 [[kr.debop4s.timeperiod.TimePeriod]]의 시작 시각만 포함될 때 */
  val StartInside = Value("StartInside")
  /**
   * 현 [[kr.debop4s.timeperiod.TimePeriod]]의 시작 시각과 대상 [[kr.debop4s.timeperiod.TimePeriod]]의 시작 시각이 일치하고,
   * 대상 [[kr.debop4s.timeperiod.TimePeriod]] 가 현 [[kr.debop4s.timeperiod.TimePeriod]]에 포함될 때
   */
  val InsideStartTouching = Value("InsideStartTouching")
  /**
   * 현 [[kr.debop4s.timeperiod.TimePeriod]]의 시작 시각과 대상 [[kr.debop4s.timeperiod.TimePeriod]]의 시작 시각이 일치하고,
   * 현 [[kr.debop4s.timeperiod.TimePeriod]] 가 대상 [[kr.debop4s.timeperiod.TimePeriod]]에 포함될 때
   */
  val EnclosingStartTouching = Value("EnclosingStartTouching")

  /** 현 [[kr.debop4s.timeperiod.TimePeriod]]가 대상 [[kr.debop4s.timeperiod.TimePeriod]] 기간에 포함될 때 */
  val Enclosing = Value("Enclosing")

  /**
   * 현 [[kr.debop4s.timeperiod.TimePeriod]]의 완료 시각과 대상 [[kr.debop4s.timeperiod.TimePeriod]]의 완료 시각이 일치하고,
   * 현 [[kr.debop4s.timeperiod.TimePeriod]] 가 대상 [[kr.debop4s.timeperiod.TimePeriod]]에 포함될 때
   */
  val EnclosingEndTouching = Value("EnclosingEndTouching")

  /**
   * 현 [[kr.debop4s.timeperiod.TimePeriod]] 기간과 대상 [[kr.debop4s.timeperiod.TimePeriod]]의 기간이 일치할 때,
   * 둘 다 Anytime이라도 ExactMath가 된다.
   */
  val ExactMatch = Value("ExactMatch")

  /** 현 기간안에 대상 기간이 내부에 포함될 때 */
  val Inside = Value("Inside")

  /** 현 기간 안에 대상 기간이 포함되는데, 완료시각만 같을 때 */
  val InsideEndTouching = Value("InsideEndTouching")

  /** 현 기간 안에 대상 기간의 완료 시각만 포함될 때 */
  val EndInside = Value("EndInside")

  /** 현 기간의 시작 시각이 대상 기간의 완료 시각과 일치할 때 */
  val EndTouching = Value("EndTouching")

  /** 대상 기간의 완료 시각이 현 기간의 시작시간 전에 있을 때 */
  val Before = Value("Before")

}

object PeriodUnit extends Enumeration {

  type PeriodUnit = Value

  val All = Value("All")
  val Year = Value("Year")
  val Halfyear = Value("Halfyear")
  val Quarter = Value("Quarter")
  val Month = Value("Month")
  val Week = Value("Week")
  val Day = Value("Day")
  val Hour = Value("Hour")
  val Minute = Value("Minute")
  val Second = Value("Second")
  val Millisecond = Value("Millisecond")
}

object Quarter extends Enumeration {

  type Quarter = Value

  val First = Value(1, "1Q")
  val Second = Value(2, "2Q")
  val Third = Value(3, "3Q")
  val Fourth = Value(4, "4Q")
}

object SeekBoundaryMode extends Enumeration {

  type SeekBoundaryMode = Value

  val Fill = Value("Fill")
  val Next = Value("Next")
}

object SeekDirection extends Enumeration {
  type SeekDirection = Value

  val Forward = Value(1, "Forward")
  val Backward = Value(-1, "Backward")
}