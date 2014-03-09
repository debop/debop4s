package com.github.debop4s.data.model

import javax.persistence.Embedded


/**
 * Tree Node 를 표현하는 Entity
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 24. 오후 4:13
 */
trait HibernateTreeEntity[T <: HibernateTreeEntity[T]] extends PersistentObject {

  def self: T

  /**
  * 부모 노드
  */
  def getParent: T

  /**
  * 부모 노드
  */
  def setParent(parent: T)

  /**
  * 자식 노드 컬렉션
  */
  val children: java.util.Set[T] = new java.util.HashSet[T]

  /**
  * 트리 상에서의 현재 노드의 위치
  */
  @Embedded
  val nodePosition = new TreeNodePosition()

  /**
  * 자식 노드 추가
  */
  def addChild(child: T) {
    child.setParent(self)
    children.add(child)
  }

  /**
  * 자식 노드 삭제
  */
  def removeChild(child: T) {
    require(child != null)
    if (children.remove(child))
      child.setParent(null.asInstanceOf[T])
  }
}
