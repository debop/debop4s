package debop4s.data

import debop4s.core.io.Serializers
import debop4s.data.hibernate.tools.CriteriaTool
import java.util.Date
import java.util.{ Map => JMap, Collection => JCollection, List => JList }
import org.hibernate.criterion._
import org.hibernate.internal.CriteriaImpl
import org.hibernate.{ StatelessSession, Query, Session, Criteria }
import scala.Some
import scala.annotation.varargs
import scala.collection.JavaConversions._

/**
 * package
 * @author debop created at 2014. 5. 20.
 */
package object hibernate {

  implicit class CriteriaExtensions(val criteria: Criteria) {

    def addEq(propertyName: String, value: Any): Criteria =
      criteria.add(Restrictions.eq(propertyName, value))

    def addEqOrIsNull(propertyName: String, value: Any): Criteria =
      criteria.add(Restrictions.eqOrIsNull(propertyName, value))

    def addNotEq(propertyName: String, value: Any): Criteria =
      criteria.add(Restrictions.not(Restrictions.eq(propertyName, value)))

    def addLe(propertyName: String, value: Any): Criteria =
      criteria.add(Restrictions.le(propertyName, value))

    def addLeProperty(propertyName: String, otherPropertyName: String): Criteria =
      criteria.add(Restrictions.leProperty(propertyName, otherPropertyName))

    def addLt(propertyName: String, value: Any): Criteria =
      criteria.add(Restrictions.lt(propertyName, value))

    def addLtProperty(propertyName: String, otherPropertyName: String): Criteria =
      criteria.add(Restrictions.ltProperty(propertyName, otherPropertyName))

    def addGe(propertyName: String, value: Any): Criteria =
      criteria.add(Restrictions.ge(propertyName, value))

    def addGeProperty(propertyName: String, otherPropertyName: String): Criteria =
      criteria.add(Restrictions.geProperty(propertyName, otherPropertyName))

    def addGt(propertyName: String, value: Any): Criteria =
      criteria.add(Restrictions.gt(propertyName, value))

    def addGtProperty(propertyName: String, otherPropertyName: String): Criteria =
      criteria.add(Restrictions.gtProperty(propertyName, otherPropertyName))

    def addAllEq(propertyNameValues: JMap[String, _]): Criteria =
      criteria.add(Restrictions.allEq(propertyNameValues))

    def addIsEmpty(propertyName: String): Criteria =
      criteria.add(Restrictions.isEmpty(propertyName))

    def addIsNotEmpty(propertyName: String): Criteria =
      criteria.add(Restrictions.isNotEmpty(propertyName))

    def addIsNull(propertyName: String): Criteria =
      criteria.add(Restrictions.isNull(propertyName))

    def addIsNotNull(propertyName: String): Criteria =
      criteria.add(Restrictions.isNotNull(propertyName))

    def addLike(propertyName: String, value: String): Criteria =
      addLike(propertyName, value, MatchMode.START)

    def addLike(propertyName: String, value: String, matchMode: MatchMode): Criteria =
      criteria.add(Restrictions.like(propertyName, value, matchMode))

    def addILike(propertyName: String, value: String): Criteria =
      addILike(propertyName, value, MatchMode.START)

    def addILike(propertyName: String, value: String, matchMode: MatchMode): Criteria =
      criteria.add(Restrictions.ilike(propertyName, value, matchMode))

    def addIdEq(idValue: Any): Criteria =
      criteria.add(Restrictions.idEq(idValue))

    def addIn(propertyName: String, ids: JCollection[_]): Criteria =
      criteria.add(Restrictions.in(propertyName, ids))

    def addIn(propertyName: String, ids: Array[AnyRef]): Criteria =
      criteria.add(Restrictions.in(propertyName, ids))

    /**
     * 속성명이 상하한 값 사이에 있는지 검사흐는 질의를 추가합니다. SQL의 BETWEEN과 같다.
     *
     * @param propertyName 속성 명
     * @param lo           하한 값
     * @param hi           상한 값
     * @param includeLo    하한 값 포함 여부
     * @param includeHi    상한 값 포함 여부
     * @return Criteria
     */
    def addBetween(propertyName: String,
                   lo: AnyRef,
                   hi: AnyRef,
                   includeLo: Boolean,
                   includeHi: Boolean): Criteria = {
      criteria.add(CriteriaTool.getBetween(propertyName, lo, hi, includeLo, includeHi))
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
      criteria.add(CriteriaTool.getInRange(loPropertyName, hiPropertyName, value, includeLo, includeHi))
    }
    /**
     * Database 의 상하한 값과 질의를 하는 상하한 값이 겹치는 부분이 있는지 검사하는 질의어를 만듭니다.
     *
     * @param loPropertyName 하한 값을 나타내는 속성
     * @param hiPropertyName 상한 값을 나타내는 속성
     * @param lo             하한 값
     * @param hi             상한 값
     * @param includeLo      하한 값 포함 여부
     * @param includeHi      상한 값 포함 여부
     * @return Criteria
     */
    def addOverlap(loPropertyName: String,
                   hiPropertyName: String,
                   lo: AnyRef,
                   hi: AnyRef,
                   includeLo: Boolean,
                   includeHi: Boolean): Criteria = {
      criteria.add(CriteriaTool.getOverlap(loPropertyName, hiPropertyName, lo, hi, includeLo, includeHi))
    }
    /** 지정된 시각 이전인 경우 */
    def addElapsed(propertyName: String, moment: Date): Criteria =
      criteria.add(Restrictions.lt(propertyName, moment))

    /** 지정된 시각 이후인 경우 */
    def addFutures(propertyName: String, moment: Date): Criteria =
      criteria.add(Restrictions.gt(propertyName, moment))

    /** 속성 값이 null인 경우는 false로 간주하고, value와 같은 값을 가지는 질의어를 추가합니다. */
    def addNullAsFalse(propertyName: String, value: Option[Boolean]): Criteria = {
      if (value.isEmpty || value.get)
        addEq(propertyName, true)
      else
        criteria.add(CriteriaTool.getEqIncludeNull(propertyName, false))
    }
    /** 속성 값이 null 인 경우는 true로 간주하고, value와 같은 값을 가지는 질의어를 추가합니다. */
    def addNullAsTrue(propertyName: String, value: Option[Boolean]): Criteria = {
      val v = if (value.isEmpty) Some(false) else value

      if (v.get) addEq(propertyName, false)
      else criteria.add(CriteriaTool.getEqIncludeNull(propertyName, true))
    }

    def addNot(expression: Criterion): Criteria =
      criteria.add(Restrictions.not(expression))

    def copy(): Criteria =
      Serializers.copyObject(criteria.asInstanceOf[CriteriaImpl])

    @varargs
    def addOrders(orders: Order*): Criteria = {
      orders.foreach(criteria.addOrder)
      criteria
    }

    def addOrders(orders: java.lang.Iterable[Order]): Criteria = {
      orders.foreach(criteria.addOrder)
      criteria
    }
    @varargs
    def addCriterions(criterions: Criterion*): Criteria = {
      criterions.foreach(criteria.add)
      criteria
    }
    def addCriterions(criterions: Iterable[Criterion]): Criteria = {
      criterions.foreach(criteria.add)
      criteria
    }

    def setFirstResult(firstResult: Int): Criteria = {
      if (firstResult >= 0) criteria.setFirstResult(firstResult)
      criteria
    }

    def setMaxResults(maxResults: Int): Criteria = {
      if (maxResults > 0) criteria.setMaxResults(maxResults)
      criteria
    }

    def setPaging(firstResult: Int, maxResults: Int): Criteria = {
      setFirstResult(firstResult)
      setMaxResults(maxResults)
    }
  }

  implicit class DetachedCriteriaExtensions(val dc: DetachedCriteria) {
    def addEq(propertyName: String, value: Any): DetachedCriteria =
      dc.add(Restrictions.eq(propertyName, value))

    def addEqOrIsNull(propertyName: String, value: Any): DetachedCriteria =
      dc.add(Restrictions.eqOrIsNull(propertyName, value))

    def addNotEq(propertyName: String, value: Any): DetachedCriteria =
      dc.add(Restrictions.not(Restrictions.eq(propertyName, value)))

    def addLe(propertyName: String, value: Any): DetachedCriteria =
      dc.add(Restrictions.le(propertyName, value))

    def addLeProperty(propertyName: String, otherPropertyName: String): DetachedCriteria =
      dc.add(Restrictions.leProperty(propertyName, otherPropertyName))

    def addLt(propertyName: String, value: Any): DetachedCriteria =
      dc.add(Restrictions.lt(propertyName, value))

    def addLtProperty(propertyName: String, otherPropertyName: String): DetachedCriteria =
      dc.add(Restrictions.ltProperty(propertyName, otherPropertyName))

    def addGe(propertyName: String, value: Any): DetachedCriteria =
      dc.add(Restrictions.ge(propertyName, value))

    def addGeProperty(propertyName: String, otherPropertyName: String): DetachedCriteria =
      dc.add(Restrictions.geProperty(propertyName, otherPropertyName))

    def addGt(propertyName: String, value: Any): DetachedCriteria =
      dc.add(Restrictions.gt(propertyName, value))

    def addGtProperty(propertyName: String, otherPropertyName: String): DetachedCriteria =
      dc.add(Restrictions.gtProperty(propertyName, otherPropertyName))

    def addAllEq(propertyNameValues: JMap[String, _]): DetachedCriteria =
      dc.add(Restrictions.allEq(propertyNameValues))

    def addIsEmpty(propertyName: String): DetachedCriteria =
      dc.add(Restrictions.isEmpty(propertyName))

    def addIsNotEmpty(propertyName: String): DetachedCriteria =
      dc.add(Restrictions.isNotEmpty(propertyName))

    def addIsNull(propertyName: String): DetachedCriteria =
      dc.add(Restrictions.isNull(propertyName))

    def addIsNotNull(propertyName: String): DetachedCriteria =
      dc.add(Restrictions.isNotNull(propertyName))

    def addLike(propertyName: String, value: String): DetachedCriteria =
      addLike(propertyName, value, MatchMode.START)

    def addLike(propertyName: String, value: String, matchMode: MatchMode): DetachedCriteria =
      dc.add(Restrictions.like(propertyName, value, matchMode))

    def addILike(propertyName: String, value: String): DetachedCriteria =
      addILike(propertyName, value, MatchMode.START)

    def addILike(propertyName: String, value: String, matchMode: MatchMode): DetachedCriteria =
      dc.add(Restrictions.ilike(propertyName, value, matchMode))

    def addIdEq(idValue: Any): DetachedCriteria =
      dc.add(Restrictions.idEq(idValue))

    def addIn(propertyName: String, ids: JCollection[_]): DetachedCriteria =
      dc.add(Restrictions.in(propertyName, ids))

    def addIn(propertyName: String, ids: Array[AnyRef]): DetachedCriteria =
      dc.add(Restrictions.in(propertyName, ids))

    def addBetween(propertyName: String,
                   lo: AnyRef,
                   hi: AnyRef,
                   includeLo: Boolean,
                   includeHi: Boolean): DetachedCriteria = {
      dc.add(CriteriaTool.getBetween(propertyName, lo, hi, includeLo, includeHi))
    }

    def addInRange(loPropertyName: String,
                   hiPropertyName: String,
                   value: Any,
                   includeLo: Boolean,
                   includeHi: Boolean): DetachedCriteria = {
      dc.add(CriteriaTool.getInRange(loPropertyName, hiPropertyName, value, includeLo, includeHi))
    }

    def addOverlap(loPropertyName: String,
                   hiPropertyName: String,
                   lo: AnyRef,
                   hi: AnyRef): DetachedCriteria = {
      addOverlap(loPropertyName, hiPropertyName, lo, hi, includeLo = true, includeHi = true)
    }

    def addOverlap(loPropertyName: String,
                   hiPropertyName: String,
                   lo: AnyRef,
                   hi: AnyRef,
                   includeLo: Boolean,
                   includeHi: Boolean): DetachedCriteria = {
      dc.add(CriteriaTool.getOverlap(loPropertyName, hiPropertyName, lo, hi, includeLo, includeHi))
    }

    /** 지정된 시각 이전인 경우 */
    def addElapsed(propertyName: String, moment: Date): DetachedCriteria = {
      dc.add(Restrictions.lt(propertyName, moment))
    }

    /** 지정된 시각 이후인 경우 */
    def addFutures(propertyName: String, moment: Date): DetachedCriteria = {
      dc.add(Restrictions.gt(propertyName, moment))
    }

    /** 속성 값이 null 이면 false 로 간주하고 value와 같은 값을 찾습니다. */
    def addNullAsFalse(propertyName: String, value: Option[Boolean] = None): DetachedCriteria = {
      val v = if (value.isEmpty) true else value.get

      if (v) addEq(propertyName, true)
      else dc.add(CriteriaTool.getEqIncludeNull(propertyName, false))
    }

    /** 속성 값이 null 이면 true로 간주하고 value와 같은 값을 찾습니다. */
    def addNullAsTrue(propertyName: String, value: Option[Boolean] = None): DetachedCriteria = {
      val v = if (value.isEmpty) true else value.get

      if (v) addEq(propertyName, false)
      else dc.add(CriteriaTool.getEqIncludeNull(propertyName, true))
    }

    /** 질의에 NOT 을 추가합니다. */
    def addNot(expression: Criterion): DetachedCriteria = {
      dc.add(Restrictions.not(expression))
    }

    def copy(): DetachedCriteria = Serializers.copyObject(dc)

    @varargs
    def getExecutableCriteria(session: Session, orders: Order*): Criteria = {
      dc.addOrders(orders: _*).getExecutableCriteria(session)
    }
    def getExecutableCriteria(session: Session, orders: java.lang.Iterable[Order]): Criteria = {
      dc.addOrders(orders).getExecutableCriteria(session)
    }

    @varargs
    def addOrders(orders: Order*): DetachedCriteria = {
      orders.foreach { o => dc.addOrder(o) }
      dc
    }

    def addOrders(orders: java.lang.Iterable[Order]): DetachedCriteria = {
      orders.foreach { o => dc.addOrder(o) }
      dc
    }
  }

  implicit class QueryExtensions(val query: Query) {

    @varargs
    def setParameters(parameters: HibernateParameter*): Query = {
      parameters.foreach { p =>
        query.setParameter(p.getName, p.getValue)
      }
      query
    }
    def setParameters(query: Query, parameters: java.lang.Iterable[HibernateParameter]): Query = {
      for (p <- parameters) {
        query.setParameter(p.getName, p.getValue)
      }
      query
    }

    def setFirstResult(firstResult: Int): Query = {
      if (firstResult >= 0) query.setFirstResult(firstResult)
      query
    }

    def setMaxResults(maxResults: Int): Query = {
      if (maxResults > 0) query.setMaxResults(maxResults)
      query
    }

    def setPaging(firstResult: Int, maxResults: Int): Query = {
      setFirstResult(firstResult)
      setMaxResults(maxResults)
    }
  }


  implicit class SessionExtensions(val session: Session) {

    def openStatelessSession(): StatelessSession =
      session.getSessionFactory.openStatelessSession()
  }
}
