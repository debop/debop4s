package com.github.debop4s.data.hibernate.tools

import org.hibernate.Criteria
import org.hibernate.criterion.{Order, DetachedCriteria, Restrictions}
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

    def addIn(criteria: Criteria, propertyName: String, ids: java.io.Serializable*): Criteria =
        criteria.add(Restrictions.in(propertyName, ids))

    def addIn(criteria: Criteria, propertyName: String, ids: Array[Any]): Criteria =
        criteria.add(Restrictions.in(propertyName, ids.toSeq))

    def addIn(dc: DetachedCriteria, propertyName: String, ids: java.io.Serializable*): DetachedCriteria =
        dc.add(Restrictions.in(propertyName, ids))

    def addIn(dc: DetachedCriteria, propertyName: String, ids: Array[Any]): DetachedCriteria =
        dc.add(Restrictions.in(propertyName, ids.toSeq))


    def toOrders(sort: Sort): Seq[Order] = {
        sort.map { x =>
            if (x.getDirection == Sort.Direction.ASC)
                Order.asc(x.getProperty)
            else
                Order.desc(x.getProperty)
        }.toSeq
    }
}
