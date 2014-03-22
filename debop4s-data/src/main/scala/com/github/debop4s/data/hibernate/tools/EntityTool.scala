package com.github.debop4s.data.hibernate.tools

import com.github.debop4s.core.ValueObject
import com.github.debop4s.core.json.JacksonSerializer
import com.github.debop4s.core.utils.{Graphs, Mappers, Strings}
import com.github.debop4s.data.hibernate.repository.HibernateDao
import com.github.debop4s.data.model.HibernateTreeEntity
import org.hibernate.criterion.{Restrictions, DetachedCriteria}
import org.slf4j.LoggerFactory
import scala.collection.JavaConversions._
import scala.collection.mutable.ArrayBuffer
import scala.reflect.ClassTag

/**
 * 엔티티를 조작하는데 필요한 메소드를 제공합니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 25. 오후 3:28
 */
object EntityTool {

    private lazy val log = LoggerFactory.getLogger(getClass)

    private lazy val json = JacksonSerializer()

    def asString(entity: ValueObject): String =
        if (entity == null) Strings.NULL_STR else entity.toString


    def asJson(entity: ValueObject): String =
        json.serializeToText(entity)

    def mapEntity[S <: AnyRef, T <: AnyRef](source: S, target: T) {
        Mappers.map(source, target)
    }

    def mapEntity[S <: AnyRef, T <: AnyRef : ClassTag](source: S): T =
        Mappers.map[T](source)

    def mapEntityAll[T <: AnyRef : ClassTag](sources: Iterable[_]) =
        Mappers.mapAll[T](sources)

    //
    // HINT: Hibernate Session은 Multi thread 에서는 예외가 발생합니다.
    // HINT: 모든 엔티티에 대해 Hibernate#initialize 를 수행해주시고, 사용하세요.
    //
    def mapEntityAllAsync[T <: AnyRef : ClassTag](sources: Iterable[_]) =
        Mappers.mapAllAsync[T](sources)

    //
    // HINT: Hibernate Session은 Multi thread 에서는 예외가 발생합니다.
    // HINT: 모든 엔티티에 대해 Hibernate#initialize 를 수행해주시고, 사용하세요.
    //
    def mapEntityAsParallel[T <: AnyRef : ClassTag](sources: Iterable[_]) =
        Mappers.mapAllAsParallel[T](sources)


    /**
    * 트리상의 노드 위치를 갱신합니다.
    */
    def updateTreeNodePosition[T <: HibernateTreeEntity[T]](entity: T) {
        require(entity != null)

        val np = entity.nodePosition

        if (entity.getParent != null) {
            np.lvl = entity.getParent.nodePosition.lvl + 1
            if (!entity.getParent.children.contains(entity)) {
                np.ord = entity.getParent.children.size
            }
        } else {
            np.setPosition(0, 0)
        }
    }

    def getChildCount[T <: HibernateTreeEntity[T]](dao: HibernateDao, entity: T): Long = {
        require(entity != null)
        val dc = DetachedCriteria.forClass(entity.getClass)
        dc.add(Restrictions.eq("parent", entity))
        dao.count(dc)
    }

    def hasChildren[T <: HibernateTreeEntity[T]](dao: HibernateDao, entity: T): Boolean = {
        require(entity != null)
        val dc = DetachedCriteria.forClass(entity.getClass)
        dc.add(Restrictions.eq("parent", entity))

        dao.exists(dc)
    }

    def setNodeOrder[T <: HibernateTreeEntity[T]](node: T, order: Int = 0) {
        require(node != null)

        if (node.getParent != null) {
            node.getParent.children.foreach { child =>
                if (child.nodePosition.ord >= order) {
                    child.nodePosition.ord = child.nodePosition.ord + 1
                }
            }
            node.nodePosition.ord = math.max(0, math.min(order, node.getParent.children.size - 1))
        } else {
            node.nodePosition.ord = math.max(0, order)
        }
    }

    def changeParent[T <: HibernateTreeEntity[T]](node: T, oldParent: T, newParent: T) {
        require(node != null)

        if (oldParent != null) {
            oldParent.children.add(node)
        }

        if (newParent != null) {
            newParent.children.add(node)
        }

        node.setParent(newParent)
        updateTreeNodePosition(node)
    }

    def setParent[T <: HibernateTreeEntity[T]](node: T, parent: T) {
        require(node != null)
        changeParent(node, node.getParent, parent)
    }

    def insertChildNode[T <: HibernateTreeEntity[T]](parent: T, child: T, order: Int) {
        require(parent != null)
        require(child != null)

        val ord = math.max(0, math.min(order, parent.children.size - 1))
        parent.addChild(child)
        setNodeOrder(child, ord)
    }

    /**
    * Tree 상에서 현재 노드의 모든 조상 노드를 구합니다.
    */
    def ancestors[T <: HibernateTreeEntity[T]](node: T): IndexedSeq[T] = {
        val ans = ArrayBuffer[T]()
        if (node != null) {
            var parent = node
            while (parent != null) {
                ans += parent
                parent = parent.getParent
            }
        }
        ans
    }

    /**
    * Tree 상에서 현재 노드의 모든 자손 노드를 구합니다.
    */
    def descendents[T <: HibernateTreeEntity[T]](node: T): Seq[T] = {
        Graphs.depthFirstScan[T](node, x => x.children)
    }

    /**
     * 트리 상의 현재 노드의 최상위 root node 를 구합니다.
     */
    def getRoot[T <: HibernateTreeEntity[T]](node: T): T = {
        if (node == null)
            return node

        var current = node
        while (current.getParent != null) {
            current = current.getParent
        }
        current
    }

}
