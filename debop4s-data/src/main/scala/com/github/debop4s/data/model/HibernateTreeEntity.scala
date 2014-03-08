package com.github.debop4s.data.model


/**
 * Tree Node 를 표현하는 Entity
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 24. 오후 4:13
 */
trait HibernateTreeEntity[T <: HibernateTreeEntity[T]] extends PersistentObject {

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
  def getChildren: java.util.Set[T]

  /**
  * 트리 상에서의 현재 노드의 위치
  */
  def getNodePosition: TreeNodePosition

  /**
  * 자식 노드 추가
  */
  def addChild(child: T)

  /**
  * 자식 노드 삭제
  */
  def removeChild(child: T)
}
