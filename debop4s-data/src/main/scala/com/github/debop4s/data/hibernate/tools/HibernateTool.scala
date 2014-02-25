package com.github.debop4s.data.hibernate.tools

import com.github.debop4s.core.io.Serializers
import com.github.debop4s.data.hibernate.HibernateParameter
import org.hibernate.criterion.{Order, DetachedCriteria}
import org.hibernate.{Query, Session, Criteria}
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable

/**
 * com.github.debop4s.data.hibernate.tools.HibernateTool
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 24. 오후 9:24
 */
object HibernateTool {

    private lazy val log = LoggerFactory.getLogger(getClass)

    def copyDetachedCriteria(src: DetachedCriteria) = Serializers.copyObject(src)

    def copyCriteria(src: Criteria) = Serializers.copyObject(src)

    def getExecutableCriteria(session: Session, dc: DetachedCriteria, orders: Order*): Criteria =
        addOrders(dc, orders: _*).getExecutableCriteria(session)

    def addOrders(dc: DetachedCriteria, orders: Order*): DetachedCriteria = {
        assert(dc != null)
        if (orders != null)
            orders.foreach(o => dc.addOrder(o))
        dc
    }

    def addOrders(criteria: Criteria, orders: Order*): Criteria = {
        assert(criteria != null)
        if (orders != null)
            orders.foreach(o => criteria.addOrder(o))
        criteria
    }

    def setParameters(query: Query, parameters: HibernateParameter*): Query = {
        assert(query != null)
        if (parameters != null)
            parameters.foreach(p => query.setParameter(p.name, p.value))
        query
    }

    def setPaging(criteria: Criteria, pageable: Pageable): Criteria =
        setPaging(criteria, pageable.getPageNumber * pageable.getPageSize, pageable.getPageSize)

    def setPaging(criteria: Criteria, firstResult: Int, maxResults: Int): Criteria = {
        if (firstResult >= 0)
            criteria.setFirstResult(firstResult)
        if (maxResults > 0)
            criteria.setMaxResults(maxResults)

        criteria
    }

    def setPaging(query: Query, pageable: Pageable): Query =
        setPaging(query, pageable.getPageNumber * pageable.getPageSize, pageable.getPageSize)

    def setPaging(query: Query, firstResult: Int, maxResults: Int): Query = {
        if (firstResult >= 0)
            query.setFirstResult(firstResult)
        if (maxResults > 0)
            query.setMaxResults(maxResults)

        query
    }

}
