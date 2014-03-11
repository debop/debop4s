package com.github.debop4s.data.jpa.repository

import com.mysema.query.jpa.impl.{JPAUpdateClause, JPADeleteClause, JPAQuery}
import com.mysema.query.types.{OrderSpecifier, EntityPath}
import javax.persistence.{PersistenceContext, EntityManager}
import org.slf4j.LoggerFactory
import org.springframework.data.domain.{PageImpl, PageRequest, Page}
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

/**
 * JpaQueryDslDaoHelper
 * Created by debop on 2014. 1. 29..
 */
@Repository
@Transactional(readOnly = true)
class JpaQueryDslDao {

  private lazy val log = LoggerFactory.getLogger(getClass)

  @PersistenceContext val em: EntityManager = null

  def findOne[T](entityClass: Class[T], id: AnyRef) = em.find(entityClass, id)

  def findAll[T](path: EntityPath[T], query: JPAQuery, firstResult: Int, maxResults: Int): java.util.List[T] =
    query.offset(firstResult)
    .limit(maxResults)
    .list(path)

  def getPage[T <: Comparable[_]](path: EntityPath[T],
                                  query: JPAQuery,
                                  pageNo: Int,
                                  pageSize: Int,
                                  orders: OrderSpecifier[T]*): Page[T] = {
    val totalCount = query.count()
    val entities = query.offset(pageNo * pageSize)
                   .limit(pageSize)
                   .orderBy(orders: _*)
                   .list(path)

    new PageImpl[T](entities, new PageRequest(pageNo, pageSize), totalCount)
  }

  def getQuery: JPAQuery = new JPAQuery(em)

  def from(path: EntityPath[_]): JPAQuery = getQuery.from(path)

  def from(paths: EntityPath[_]*): JPAQuery = getQuery.from(paths: _*)

  def deleteFrom(path: EntityPath[_]) = new JPADeleteClause(em, path)

  def updateFrom(path: EntityPath[_]) = new JPAUpdateClause(em, path)
}
