package com.github.debop4s.data.utils

import com.github.debop4s.data.hibernate.repository.HibernateDao
import com.github.debop4s.data.model.HibernateTreeEntity
import org.hibernate.criterion.{Restrictions, DetachedCriteria}
import org.slf4j.LoggerFactory
import scala.collection.JavaConversions._

/**
 * com.github.debop4s.data.hibernate.tools.Entities
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 24. 오후 8:38
 */
object Entities {

  lazy val log = LoggerFactory.getLogger(getClass)

  def updateTreeNodePosition[T <: HibernateTreeEntity[T]](entity: T) {
    assert(entity != null)
    val np = entity.nodePosition
    if (entity.getParent != null) {
      np.lvl = entity.getParent.nodePosition.lvl + 1
      if (!entity.getParent.children.contains(entity)) {
        np.ord = entity.getParent.children.size
      }
    } else {
      np.lvl = 0
      np.ord = 0
    }
  }

  def getChildCount[T <: HibernateTreeEntity[T]](dao: HibernateDao, entity: T) = {
    val dc = DetachedCriteria.forClass(entity.getClass)
    dc.add(Restrictions.eq("parent", entity))
    dao.count(dc)
  }

  def hasChildren[T <: HibernateTreeEntity[T]](dao: HibernateDao, entity: T): Boolean = {
    val dc = DetachedCriteria.forClass(entity.getClass)
    dc.add(Restrictions.eq("parent", entity))
    dao.exists(dc)
  }

  def setNodeOrder[T <: HibernateTreeEntity[T]](node: T, order: Int) {
    assert(node != null)
    if (node.getParent != null) {
      node.getParent.children.foreach(child => {
        if (child.nodePosition.ord >= order)
          child.nodePosition.ord = child.nodePosition.ord + 1
      })
    }
    node.nodePosition.ord = order
  }

  def setNodeOrder[T <: HibernateTreeEntity[T]](parent: T) {
    assert(parent != null)

    var order = 0
    parent.children.toList
    .sortWith(_.nodePosition.ord < _.nodePosition.ord)
    .foreach(n => {
      n.nodePosition.ord = order
      order += 1
    })
  }

  def changeParent[T <: HibernateTreeEntity[T]](node: T, oldParent: T, newParent: T) {
    assert(node != null)

    if (oldParent != null)
      oldParent.removeChild(node)
    if (newParent != null)
      newParent.addChild(node)

    node.setParent(newParent)
    updateTreeNodePosition(node)
  }

  def setParent[T <: HibernateTreeEntity[T]](node: T, parent: T) {
    assert(node != null)
    changeParent(node, node.getParent, parent)
  }

  def insertChildNode[T <: HibernateTreeEntity[T]](parent: T, child: T, order: Int) {
    assert(parent != null)
    assert(child != null)

    val ord = Math.max(0, Math.min(order, parent.children.size - 1))
    parent.addChild(child)
    setNodeOrder(child, ord)
  }

}
