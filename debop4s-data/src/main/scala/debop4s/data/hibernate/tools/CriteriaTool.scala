package debop4s.data.hibernate.tools

import debop4s.core.utils.Strings
import java.util.Date
import java.util.{ Map => JMap, Collection => JCollection }
import org.hibernate.Criteria
import org.hibernate.criterion._
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Sort
import scala.collection.JavaConversions._

/**
 * [[Criteria]] 빌드를 위한 Helper Object 입니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 24. 오후 6:09
 */
object CriteriaTool {

  private lazy val log = LoggerFactory.getLogger(getClass)

  def toOrders(sort: Sort): Seq[Order] = {
    sort.map { (x: Sort.Order) =>
      if (x.getDirection == Sort.Direction.ASC)
        Order.asc(x.getProperty)
      else
        Order.desc(x.getProperty)
    }.toSeq
  }

  /**
   * 속성 값이 lo, hi 사이의 값인지를 검사하는 질의어
   *
   * @param propertyName 속성 명
   * @param lo           하한 값
   * @param hi           상한 값
   * @return the is between criterion
   */
  def getBetween(propertyName: String, lo: AnyRef, hi: AnyRef): Criterion = {
    getBetween(propertyName, lo, hi, includeLo = true, includeHi = true)
  }
  /**
   * 속성 값이 lo, hi 사이의 값인지를 검사하는 질의어
   *
   * @param propertyName 속성 명
   * @param lo           하한 값
   * @param hi           상한 값
   * @param includeLo    하한 값 포함 여부
   * @param includeHi    상한 값 포함 여부
   * @return the is between criterion
   */
  def getBetween(propertyName: String, lo: AnyRef, hi: AnyRef, includeLo: Boolean, includeHi: Boolean): Criterion = {
    if (lo == null && hi == null) throw new IllegalArgumentException("상하한 값 모두 Null이면 안됩니다.")
    if (lo != null && hi != null) return Restrictions.between(propertyName, lo, hi)

    val result = Restrictions.conjunction

    if (lo != null) result.add(
                                if (includeLo) Restrictions.ge(propertyName, lo)
                                else Restrictions.gt(propertyName, lo)
                              )
    if (hi != null) result.add(
                                if (includeHi) Restrictions.le(propertyName, hi)
                                else Restrictions.lt(propertyName, hi)
                              )

    result
  }
  /**
   * 지정한 값이 두 속성 값 사이에 존재하는지 여부
   *
   * @param loPropertyName the lo property name
   * @param hiPropertyName the hi property name
   * @param value          the value
   * @param includeLo      the include lo
   * @param includeHi      the include hi
   * @return the is in range criterion
   */
  def getInRange(loPropertyName: String,
                 hiPropertyName: String,
                 value: Any,
                 includeLo: Boolean,
                 includeHi: Boolean): Criterion = {
    val loCriterion =
      if (includeLo) Restrictions.le(loPropertyName, value)
      else Restrictions.lt(loPropertyName, value)

    val hiCriterion =
      if (includeHi) Restrictions.ge(hiPropertyName, value)
      else Restrictions.gt(hiPropertyName, value)

    Restrictions.conjunction
    .add(Restrictions.disjunction
         .add(Restrictions.isNull(loPropertyName))
         .add(loCriterion))
    .add(Restrictions.disjunction
         .add(Restrictions.isNull(hiPropertyName))
         .add(hiCriterion))
  }
  /**
   * 지정한 범위 값이 두 속성 값 구간과 겹치는지를 알아보기 위한 질의어
   *
   * @param loPropertyName the lo property name
   * @param hiPropertyName the hi property name
   * @param lo             the lo
   * @param hi             the hi
   * @param includeLo      the include lo
   * @param includeHi      the include hi
   * @return the is overlap criterion
   */
  def getOverlap(loPropertyName: String,
                 hiPropertyName: String,
                 lo: AnyRef,
                 hi: AnyRef,
                 includeLo: Boolean,
                 includeHi: Boolean): Criterion = {
    if (lo == null && hi == null) throw new IllegalArgumentException("lo, hi 값 모두 null 이면 질의어를 만들 수 없습니다.")
    if (lo != null && hi != null) {
      Restrictions.disjunction
      .add(getInRange(loPropertyName, hiPropertyName, lo, includeLo, includeHi))
      .add(getInRange(loPropertyName, hiPropertyName, hi, includeLo, includeHi))
      .add(getBetween(loPropertyName, lo, hi, includeLo, includeHi))
      .add(getBetween(hiPropertyName, lo, hi, includeLo, includeHi))
    }
    else if (lo != null) {
      Restrictions.disjunction
      .add(getInRange(loPropertyName, hiPropertyName, lo, includeLo, includeHi))
      .add(if (includeLo) Restrictions.ge(loPropertyName, lo) else Restrictions.gt(loPropertyName, lo))
      .add(if (includeLo) Restrictions.ge(hiPropertyName, lo) else Restrictions.gt(hiPropertyName, lo))
    }
    else {
      Restrictions.disjunction
      .add(getInRange(loPropertyName, hiPropertyName, hi, includeLo, includeHi))
      .add(if (includeHi) Restrictions.le(loPropertyName, hi) else Restrictions.lt(loPropertyName, hi))
      .add(if (includeHi) Restrictions.le(hiPropertyName, hi) else Restrictions.lt(hiPropertyName, hi))
    }
  }
  /**
   * value가 null 이 아니면, 속성값과 eq 이거나 null 인 경우 모두 구한다. value가 null 인 경우는 isNull 로 만든다.
   *
   * @param propertyName the property name
   * @param value        the value
   * @return the eq include null
   */
  def getEqIncludeNull(propertyName: String, value: Any): Criterion = {
    if (value == null) Restrictions.isNull(propertyName)
    else Restrictions.eqOrIsNull(propertyName, value)
  }
  /**
   * 대소문자 구분 없이 LIKE 를 수행합니다.
   *
   * @param propertyName 속성명
   * @param value        LIKE 검색할 값
   * @return Criterion
   */
  def getInsensitiveLikeIncludeNull(propertyName: String, value: String): Criterion = {
    getInsensitiveLikeIncludeNull(propertyName, value, MatchMode.START)
  }
  /**
   * 대소문자 구분 없이 LIKE 를 수행합니다.
   *
   * @param propertyName 속성명
   * @param value        LIKE 검색할 값
   * @param matchMode    LIKE 검색 방법 ({ @link org.hibernate.criterion.MatchMode})
   * @return Criterion
   */
  def getInsensitiveLikeIncludeNull(propertyName: String, value: String, matchMode: MatchMode): Criterion = {
    if (Strings.isWhitespace(propertyName))
      Restrictions.isEmpty(propertyName)
    else
      Restrictions.disjunction
      .add(Restrictions.ilike(propertyName, value, matchMode))
      .add(Restrictions.isEmpty(propertyName))
  }
  /**
   * Criteria 에 속성명 = value 질의를 추가합니다.
   *
   * @param criteria     criteria
   * @param propertyName 속성명
   * @param value        value
   * @return criteria
   */
  def addEq(criteria: Criteria, propertyName: String, value: Any): Criteria =
    criteria.add(Restrictions.eq(propertyName, value))

  def addEqOrIsNull(criteria: Criteria, propertyName: String, value: Any): Criteria =
    criteria.add(Restrictions.eqOrIsNull(propertyName, value))

  def addNotEq(criteria: Criteria, propertyName: String, value: Any): Criteria =
    criteria.add(Restrictions.not(Restrictions.eq(propertyName, value)))

  def addLe(criteria: Criteria, propertyName: String, value: Any): Criteria =
    criteria.add(Restrictions.le(propertyName, value))

  def addLeProperty(criteria: Criteria, propertyName: String, otherPropertyName: String): Criteria =
    criteria.add(Restrictions.leProperty(propertyName, otherPropertyName))

  def addLt(criteria: Criteria, propertyName: String, value: Any): Criteria =
    criteria.add(Restrictions.lt(propertyName, value))

  def addLtProperty(criteria: Criteria, propertyName: String, otherPropertyName: String): Criteria =
    criteria.add(Restrictions.ltProperty(propertyName, otherPropertyName))

  def addGe(criteria: Criteria, propertyName: String, value: Any): Criteria =
    criteria.add(Restrictions.ge(propertyName, value))

  def addGeProperty(criteria: Criteria, propertyName: String, otherPropertyName: String): Criteria =
    criteria.add(Restrictions.geProperty(propertyName, otherPropertyName))

  def addGt(criteria: Criteria, propertyName: String, value: Any): Criteria =
    criteria.add(Restrictions.gt(propertyName, value))

  def addGtProperty(criteria: Criteria, propertyName: String, otherPropertyName: String): Criteria =
    criteria.add(Restrictions.gtProperty(propertyName, otherPropertyName))

  def addAllEq(criteria: Criteria, propertyNameValues: JMap[String, _]): Criteria =
    criteria.add(Restrictions.allEq(propertyNameValues))

  def addIsEmpty(criteria: Criteria, propertyName: String): Criteria =
    criteria.add(Restrictions.isEmpty(propertyName))

  def addIsNotEmpty(criteria: Criteria, propertyName: String): Criteria =
    criteria.add(Restrictions.isNotEmpty(propertyName))

  def addIsNull(criteria: Criteria, propertyName: String): Criteria =
    criteria.add(Restrictions.isNull(propertyName))

  def addIsNotNull(criteria: Criteria, propertyName: String): Criteria =
    criteria.add(Restrictions.isNotNull(propertyName))

  def addLike(criteria: Criteria, propertyName: String, value: String): Criteria =
    addLike(criteria, propertyName, value, MatchMode.START)

  def addLike(criteria: Criteria, propertyName: String, value: String, matchMode: MatchMode): Criteria =
    criteria.add(Restrictions.like(propertyName, value, matchMode))

  def addILike(criteria: Criteria, propertyName: String, value: String): Criteria =
    addILike(criteria, propertyName, value, MatchMode.START)

  def addILike(criteria: Criteria, propertyName: String, value: String, matchMode: MatchMode): Criteria =
    criteria.add(Restrictions.ilike(propertyName, value, matchMode))

  def addIdEq(criteria: Criteria, idValue: Any): Criteria =
    criteria.add(Restrictions.idEq(idValue))

  def addIn(criteria: Criteria, propertyName: String, ids: JCollection[_]): Criteria =
    criteria.add(Restrictions.in(propertyName, ids))

  def addIn(criteria: Criteria, propertyName: String, ids: Array[AnyRef]): Criteria =
    criteria.add(Restrictions.in(propertyName, ids))

  /**
   * 속성명이 상하한 값 사이에 있는지 검사흐는 질의를 추가합니다. SQL의 BETWEEN과 같다.
   *
   * @param criteria     criteria
   * @param propertyName 속성 명
   * @param lo           하한 값
   * @param hi           상한 값
   * @param includeLo    하한 값 포함 여부
   * @param includeHi    상한 값 포함 여부
   * @return Criteria
   */
  def addBetween(criteria: Criteria,
                 propertyName: String,
                 lo: AnyRef,
                 hi: AnyRef,
                 includeLo: Boolean,
                 includeHi: Boolean): Criteria = {
    criteria.add(getBetween(propertyName, lo, hi, includeLo, includeHi))
  }

  /**
   * 두 속성 값이 지정한 값의 범위에 속하는지 여부
   *
   * @param criteria       criteria
   * @param loPropertyName 하한 값을 나타내는 속성
   * @param hiPropertyName 상한 값을 나타내는 속성
   * @param value          검사할 값
   * @param includeLo      하한 값 포함 여부
   * @param includeHi      상한 값 포함 여부
   * @return Criteria
   */
  def addInRange(criteria: Criteria,
                 loPropertyName: String,
                 hiPropertyName: String,
                 value: Any,
                 includeLo: Boolean,
                 includeHi: Boolean): Criteria = {
    criteria.add(getInRange(loPropertyName, hiPropertyName, value, includeLo, includeHi))
  }
  /**
   * Database 의 상하한 값과 질의를 하는 상하한 값이 겹치는 부분이 있는지 검사하는 질의어를 만듭니다.
   *
   * @param criteria       criteria
   * @param loPropertyName 하한 값을 나타내는 속성
   * @param hiPropertyName 상한 값을 나타내는 속성
   * @param lo             하한 값
   * @param hi             상한 값
   * @param includeLo      하한 값 포함 여부
   * @param includeHi      상한 값 포함 여부
   * @return Criteria
   */
  def addOverlap(criteria: Criteria,
                 loPropertyName: String,
                 hiPropertyName: String,
                 lo: AnyRef,
                 hi: AnyRef,
                 includeLo: Boolean,
                 includeHi: Boolean): Criteria = {
    criteria.add(getOverlap(loPropertyName, hiPropertyName, lo, hi, includeLo, includeHi))
  }
  /** 지정된 시각 이전인 경우 */
  def addElapsed(criteria: Criteria, propertyName: String, moment: Date): Criteria =
    criteria.add(Restrictions.lt(propertyName, moment))

  /** 지정된 시각 이후인 경우 */
  def addFutures(criteria: Criteria, propertyName: String, moment: Date): Criteria =
    criteria.add(Restrictions.gt(propertyName, moment))

  /** 속성 값이 null인 경우는 false로 간주하고, value와 같은 값을 가지는 질의어를 추가합니다. */
  def addNullAsFalse(criteria: Criteria, propertyName: String, value: Option[Boolean]): Criteria = {
    if (value.isEmpty || value.get)
      addEq(criteria, propertyName, true)
    else
      criteria.add(getEqIncludeNull(propertyName, false))
  }
  /** 속성 값이 null 인 경우는 true로 간주하고, value와 같은 값을 가지는 질의어를 추가합니다. */
  def addNullAsTrue(criteria: Criteria, propertyName: String, value: Option[Boolean]): Criteria = {
    val v = if (value.isEmpty) Some(false) else value

    if (v.get) addEq(criteria, propertyName, false)
    else criteria.add(getEqIncludeNull(propertyName, true))
  }

  def addNot(criteria: Criteria, expression: Criterion): Criteria =
    criteria.add(Restrictions.not(expression))


  def addEq(dc: DetachedCriteria, propertyName: String, value: Any): DetachedCriteria =
    dc.add(Restrictions.eq(propertyName, value))

  def addEqOrIsNull(dc: DetachedCriteria, propertyName: String, value: Any): DetachedCriteria =
    dc.add(Restrictions.eqOrIsNull(propertyName, value))

  def addNotEq(dc: DetachedCriteria, propertyName: String, value: Any): DetachedCriteria =
    dc.add(Restrictions.not(Restrictions.eq(propertyName, value)))

  def addLe(dc: DetachedCriteria, propertyName: String, value: Any): DetachedCriteria =
    dc.add(Restrictions.le(propertyName, value))

  def addLeProperty(dc: DetachedCriteria, propertyName: String, otherPropertyName: String): DetachedCriteria =
    dc.add(Restrictions.leProperty(propertyName, otherPropertyName))

  def addLt(dc: DetachedCriteria, propertyName: String, value: Any): DetachedCriteria =
    dc.add(Restrictions.lt(propertyName, value))

  def addLtProperty(dc: DetachedCriteria, propertyName: String, otherPropertyName: String): DetachedCriteria =
    dc.add(Restrictions.ltProperty(propertyName, otherPropertyName))

  def addGe(dc: DetachedCriteria, propertyName: String, value: Any): DetachedCriteria =
    dc.add(Restrictions.ge(propertyName, value))

  def addGeProperty(dc: DetachedCriteria, propertyName: String, otherPropertyName: String): DetachedCriteria =
    dc.add(Restrictions.geProperty(propertyName, otherPropertyName))

  def addGt(dc: DetachedCriteria, propertyName: String, value: Any): DetachedCriteria =
    dc.add(Restrictions.gt(propertyName, value))

  def addGtProperty(dc: DetachedCriteria, propertyName: String, otherPropertyName: String): DetachedCriteria =
    dc.add(Restrictions.gtProperty(propertyName, otherPropertyName))

  def addAllEq(dc: DetachedCriteria, propertyNameValues: JMap[String, _]): DetachedCriteria =
    dc.add(Restrictions.allEq(propertyNameValues))

  def addIsEmpty(dc: DetachedCriteria, propertyName: String): DetachedCriteria =
    dc.add(Restrictions.isEmpty(propertyName))

  def addIsNotEmpty(dc: DetachedCriteria, propertyName: String): DetachedCriteria =
    dc.add(Restrictions.isNotEmpty(propertyName))

  def addIsNull(dc: DetachedCriteria, propertyName: String): DetachedCriteria =
    dc.add(Restrictions.isNull(propertyName))

  def addIsNotNull(dc: DetachedCriteria, propertyName: String): DetachedCriteria =
    dc.add(Restrictions.isNotNull(propertyName))

  def addLike(dc: DetachedCriteria, propertyName: String, value: String): DetachedCriteria =
    addLike(dc, propertyName, value, MatchMode.START)

  def addLike(dc: DetachedCriteria, propertyName: String, value: String, matchMode: MatchMode): DetachedCriteria =
    dc.add(Restrictions.like(propertyName, value, matchMode))

  def addILike(dc: DetachedCriteria, propertyName: String, value: String): DetachedCriteria =
    addILike(dc, propertyName, value, MatchMode.START)

  def addILike(dc: DetachedCriteria, propertyName: String, value: String, matchMode: MatchMode): DetachedCriteria =
    dc.add(Restrictions.ilike(propertyName, value, matchMode))

  def addIdEq(dc: DetachedCriteria, idValue: Any): DetachedCriteria =
    dc.add(Restrictions.idEq(idValue))

  def addIn(dc: DetachedCriteria, propertyName: String, ids: JCollection[_]): DetachedCriteria =
    dc.add(Restrictions.in(propertyName, ids))

  def addIn(dc: DetachedCriteria, propertyName: String, ids: Array[AnyRef]): DetachedCriteria =
    dc.add(Restrictions.in(propertyName, ids))

  def addBetween(dc: DetachedCriteria,
                 propertyName: String,
                 lo: AnyRef,
                 hi: AnyRef,
                 includeLo: Boolean,
                 includeHi: Boolean): DetachedCriteria = {
    dc.add(getBetween(propertyName, lo, hi, includeLo, includeHi))
  }

  def addInRange(dc: DetachedCriteria,
                 loPropertyName: String,
                 hiPropertyName: String,
                 value: Any,
                 includeLo: Boolean,
                 includeHi: Boolean): DetachedCriteria = {
    dc.add(getInRange(loPropertyName, hiPropertyName, value, includeLo, includeHi))
  }

  def addOverlap(dc: DetachedCriteria,
                 loPropertyName: String,
                 hiPropertyName: String,
                 lo: AnyRef,
                 hi: AnyRef): DetachedCriteria = {
    addOverlap(dc, loPropertyName, hiPropertyName, lo, hi, includeLo = true, includeHi = true)
  }

  def addOverlap(dc: DetachedCriteria,
                 loPropertyName: String,
                 hiPropertyName: String,
                 lo: AnyRef,
                 hi: AnyRef,
                 includeLo: Boolean,
                 includeHi: Boolean): DetachedCriteria = {
    dc.add(getOverlap(loPropertyName, hiPropertyName, lo, hi, includeLo, includeHi))
  }

  /** 지정된 시각 이전인 경우 */
  def addElapsed(dc: DetachedCriteria, propertyName: String, moment: Date): DetachedCriteria = {
    dc.add(Restrictions.lt(propertyName, moment))
  }

  /** 지정된 시각 이후인 경우 */
  def addFutures(dc: DetachedCriteria, propertyName: String, moment: Date): DetachedCriteria = {
    dc.add(Restrictions.gt(propertyName, moment))
  }

  /** 속성 값이 null 이면 false 로 간주하고 value와 같은 값을 찾습니다. */
  def addNullAsFalse(dc: DetachedCriteria, propertyName: String, value: Option[Boolean] = None): DetachedCriteria = {
    val v = if (value.isEmpty) true else value.get

    if (v) addEq(dc, propertyName, true)
    else dc.add(getEqIncludeNull(propertyName, false))
  }

  /** 속성 값이 null 이면 true로 간주하고 value와 같은 값을 찾습니다. */
  def addNullAsTrue(dc: DetachedCriteria, propertyName: String, value: Option[Boolean] = None): DetachedCriteria = {
    val v = if (value.isEmpty) true else value.get

    if (v) addEq(dc, propertyName, false)
    else dc.add(getEqIncludeNull(propertyName, true))
  }

  /** 질의에 NOT 을 추가합니다. */
  def addNot(dc: DetachedCriteria, expression: Criterion): DetachedCriteria = {
    dc.add(Restrictions.not(expression))
  }
}
