package debop4s.data.orm.hibernate.utils

import java.lang.{Iterable => JIterable}
import java.util
import java.util.{Collection => JCollection, Collections, Comparator, List => JList, Locale, Map => JMap}

import debop4s.core.ValueObject
import debop4s.core.json.{JacksonSerializer, JsonSerializer}
import debop4s.core.tools.MapperTool
import debop4s.core.utils.Graphs
import debop4s.data.orm.hibernate.HibernateParameter
import debop4s.data.orm.hibernate.repository.HibernateDao
import debop4s.data.orm.model._
import org.hibernate.Session
import org.hibernate.criterion.{DetachedCriteria, Projections, Restrictions}
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._

/**
 * EntityUtils
 * @author debop created at 2014. 5. 22.
 */
object EntityUtils {

  private lazy val log = LoggerFactory.getLogger(getClass)

  private final val PROPERTY_ANCESTORS: String = "ancestors"
  private final val PROPERTY_DESCENDENTS: String = "descendents"

  private final val jsonSerializer: JsonSerializer = new JacksonSerializer

  def entityToString(entity: ValueObject): String = {
    if (entity == null) "" else entity.toString
  }

  def asJsonText(entity: ValueObject): String = {
    jsonSerializer.serializeToText(entity)
  }

  def assertNotCirculaHierarchy[T <: HierarchyEntity[T]](child: T, parent: T) {
    if (child eq parent)
      throw new IllegalArgumentException("child and parent are same.")
    if (child.getDescendents.contains(parent))
      throw new IllegalArgumentException("child has parent as descendents")

    if (parent.getAncestors.asScala.intersect(child.getDescendents.asScala).size > 0)
      throw new IllegalArgumentException("ancestors of parent and descendents of child has same thing.")
  }

  def setHierarchy[T <: HierarchyEntity[T]](child: T, oldParent: T, newParent: T) {
    assert(child != null)

    log.trace(s"현재 노드의 부모를 변경하고, 계층구조를 변경합니다... " +
              s"child=$child, oldParent=$oldParent, newParent=$newParent")

    if (oldParent != null) removeHierarchy(child, oldParent)
    if (newParent != null) setHierarchy(child, newParent)
  }

  def setHierarchy[T <: HierarchyEntity[T]](child: T, parent: T) {
    if (parent == null || child == null) return

    log.trace(s"노드의 부모 및 조상을 설정합니다. child=$child, parent=$parent")

    parent.getDescendents.add(child)
    parent.getDescendents.addAll(child.getDescendents)

    parent.getAncestors.asScala.foreach { ancestor =>
      ancestor.getDescendents.add(child)
      ancestor.getDescendents.addAll(child.getDescendents)
    }

    child.getAncestors.add(parent)
    child.getAncestors.addAll(parent.getAncestors)
  }

  def removeHierarchy[T <: HierarchyEntity[T]](child: T, parent: T) {
    if (parent == null || child == null) return
    log.trace(s"노드의 부모 및 조상을 제거합니다. child=$child, parent=$parent")

    child.getAncestors.remove(parent)
    child.getAncestors.removeAll(parent.getAncestors)

    parent.getAncestors.asScala.foreach { ancestor =>
      ancestor.getDescendents.remove(child)
      ancestor.getDescendents.removeAll(child.getDescendents)
    }

    child.getDescendents.asScala.foreach { des =>
      des.getAncestors.remove(parent)
      des.getAncestors.removeAll(parent.getAncestors)
    }
  }

  def getAncestorsCriteria[T <: HierarchyEntity[T]](entity: T,
                                                    session: Session,
                                                    entityClass: Class[T]): DetachedCriteria = {
    DetachedCriteria
    .forClass(entityClass)
    .createAlias(PROPERTY_DESCENDENTS, "des")
    .add(Restrictions.eq("des.id", entity.getId))
  }

  def getDescendentsCriteria[T <: HierarchyEntity[T]](entity: T,
                                                      session: Session,
                                                      entityClass: Class[T]): DetachedCriteria = {
    DetachedCriteria
    .forClass(entityClass)
    .createAlias(PROPERTY_ANCESTORS, "ans")
    .add(Restrictions.eq("ans.id", entity.getId))
  }

  def getAncestorIds[T <: HierarchyEntity[T]](entity: T,
                                              session: Session,
                                              entityClass: Class[T]): DetachedCriteria = {
    getAncestorsCriteria(entity, session, entityClass)
    .setProjection(Projections.distinct(Projections.id))
  }

  def getDescendentIds[T <: HierarchyEntity[T]](entity: T,
                                                session: Session,
                                                entityClass: Class[T]): DetachedCriteria = {
    getDescendentsCriteria(entity, session, entityClass)
    .setProjection(Projections.distinct(Projections.id))
  }

  /**
   * 특정 로케일 키를 가지는 엔티티를 조회하는 HQL 문.
   */
  private final val GET_LIST_BY_LOCALE_KEY: String =
    "select distinct loen from %s loen where :key in indices (loen.localeMap)"

  /**
   * 특정 로케일 속성값에 따른 엔티티를 조회하는 HQL 문.
   */
  private final val GET_LIST_BY_LOCALE_PROPERTY: String =
    "select distinct loen from %s loen join loen.localeMap locale where locale.%s = :%s"

  def copyLocale[T <: LocaleEntity[TLocaleValue], TLocaleValue <: LocaleValue](src: T, dest: T) {
    src.getLocales.asScala.foreach { locale =>
      dest.addLocaleValue(locale, src.getLocaleValue(locale))
    }
  }

  def containsLocale[T <: LocaleEntity[TLocaleValue], TLocaleValue <: LocaleValue](dao: HibernateDao,
                                                                                   entityClass: Class[T],
                                                                                   locale: Locale): JList[T] = {
    val hql = GET_LIST_BY_LOCALE_KEY.format(entityClass.getName)
    dao.findByHql(hql, new HibernateParameter("key", locale)).asInstanceOf[JList[T]]
  }

  final val GET_LIST_BY_META_KEY =
    "select distinct me from %s me where :key in indices(me.metaMap)"

  final val GET_LIST_BY_META_VALUE =
    "select distinct me from %s me join me.metaMap meta where meta.value = :value"

  def containsMetaKey[T <: MetaEntity](dao: HibernateDao, entityClass: Class[T], key: String): JList[T] = {
    val hql = String.format(GET_LIST_BY_META_KEY, entityClass.getName)
    dao.findByHql(hql, new HibernateParameter("key", key)).asInstanceOf[JList[T]]
  }

  def containsMetaValue[T <: MetaEntity](dao: HibernateDao, entityClass: Class[T], value: String): JList[T] = {
    val hql = String.format(GET_LIST_BY_META_VALUE, value)
    dao.findByHql(hql, new HibernateParameter("value", value)).asInstanceOf[JList[T]]
  }

  def mapEntity[S, T](source: S, target: T): T = {
    MapperTool.map(source, target)
    target
  }

  def mapEntity[S, T](source: S, targetClass: Class[T]): T = {
    MapperTool.createMap(source, targetClass)
  }

  def mapEntities[S, T](sources: JList[S], targets: JList[T]): JList[T] = {
    val size: Int = sources.size min targets.size

    var i = 0
    while (i < size) {
      MapperTool.map(sources.get(i), targets.get(i))
      i += 1
    }
    //    for (i <- 0 until size) {
    //      MapperTool.map(sources.get(i), targets.get(i))
    //    }

    targets
  }
  def mapEntities[S, T](sources: JList[S], targetClass: Class[T]): JList[T] = {
    val targets = new util.ArrayList[T]()

    var i = 0
    while (i < sources.size()) {
      targets.add(mapEntity(sources.get(i), targetClass))
      i += 1
    }

    targets
  }
  @Deprecated
  def mapEntitiesAsParallel[S, T](sources: JList[S], targetClass: Class[T]): JList[T] = {
    val targets = new util.ArrayList[T]()

    var i = 0
    while (i < sources.size()) {
      targets.add(mapEntity(sources.get(i), targetClass))
      i += 1
    }

    targets
  }

  def updateTreeNodePosition[T <: TreeEntity[T]](entity: T) {
    assert(entity != null)

    val np = entity.getNodePosition

    if (entity.getParent != null) {
      np.setLevel(entity.getParent.getNodePosition.getLevel + 1)
      if (!entity.getParent.getChildren.contains(entity)) {
        np.setOrder(entity.getParent.getChildren.size)
      }
    }
    else {
      np.setPosition(0, 0)
    }
  }

  def getChildCount[T <: TreeEntity[T]](dao: HibernateDao, entity: T): Long = {
    val dc = DetachedCriteria.forClass(entity.getClass)
    dc.add(Restrictions.eq("parent", entity))
    dao.count(dc)
  }

  def hasChildren[T <: TreeEntity[T]](dao: HibernateDao, entity: T): Boolean = {
    val dc: DetachedCriteria = DetachedCriteria.forClass(entity.getClass)
    dc.add(Restrictions.eq("parent", entity))
    dao.exists(dc)
  }

  def setNodeOrder[T <: TreeEntity[T]](node: T, order: Int) {
    require(node != null)

    if (node.getParent != null) {
      node.getParent.getChildren.asScala.foreach { child =>
        if (child.getNodePosition.getOrder >= order) {
          child.getNodePosition.setOrder(child.getNodePosition.getOrder + 1)
        }
      }
    }
    node.getNodePosition.setOrder(order)
  }

  def adjustChildOrders[T <: TreeEntity[T]](parent: T) {
    require(parent != null)

    val children = new util.ArrayList[T](parent.getChildren)

    Collections.sort(children, new Comparator[T] {
      def compare(o1: T, o2: T): Int = {
        o1.getNodePosition.getOrder - o2.getNodePosition.getOrder
      }
    })

    var order: Int = 0
    children.asScala.foreach { node =>
      node.getNodePosition.setOrder(order)
      order += 1
    }
  }

  def changeParent[T <: TreeEntity[T]](node: T, oldParent: T, newParent: T) {
    require(node != null)

    if (oldParent != null) {
      oldParent.getChildren.remove(node)
    }

    if (newParent != null) {
      newParent.getChildren.add(node)
    }
    node.setParent(newParent)
    updateTreeNodePosition(node)
  }

  def setParent[T <: TreeEntity[T]](node: T, parent: T) {
    require(node != null)
    changeParent(node, node.getParent, parent)
  }

  def insertChildNode[T <: TreeEntity[T]](parent: T, child: T, order: Int) {
    assert(parent != null)
    assert(child != null)

    val ord = math.max(0, math.min(order, parent.getChildren.size - 1))
    parent.addChild(child)
    setNodeOrder(child, ord)
  }

  def getAncestors[T <: TreeEntity[T]](current: T): JIterable[T] = {
    val ancestors: JList[T] = new util.ArrayList[T]()
    if (current != null) {
      var parent: T = current
      while (parent != null) {
        ancestors.add(parent)
        parent = parent.getParent
      }
    }
    ancestors
  }

  def getDescendents[T <: TreeEntity[T]](current: T): JIterable[T] = {
    Graphs.depthFirstScan(current, (x: T) => x.getChildren)
  }

  def getRoot[T <: TreeEntity[T]](current: T): T = {
    if (current == null) return current

    var root: T = current
    var parent: T = current.getParent

    while (parent != null) {
      root = parent
      parent = parent.getParent
    }
    root
  }
}
