package debop4s.data.orm.mapping.tree

import java.util
import javax.persistence._

import debop4s.core.utils.Hashs
import debop4s.data.orm.AbstractJpaJUnitSuite
import debop4s.data.orm.jpa.repository.JpaQueryDslDao
import debop4s.data.orm.model.{LongEntity, TreeEntity, TreeNodePosition}
import org.hibernate.annotations.CacheConcurrencyStrategy
import org.hibernate.{annotations => hba}
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

import scala.beans.BeanProperty

@Transactional
class TreeNodeJUnitSuite extends AbstractJpaJUnitSuite {

  @PersistenceContext val em: EntityManager = null
  @Autowired val dao: JpaQueryDslDao = null

  @Test
  def configurationTest() {
    assert(em != null)
    assert(dao != null)
  }

  @Test
  def buildTree() {
    val root = TreeNode("root")
    val child1 = TreeNode("child1")
    val child2 = TreeNode("child2")

    root.addChild(child1)
    root.addChild(child2)

    val child11 = TreeNode("child11")
    val child12 = TreeNode("child12")
    child1.addChild(child11)
    child1.addChild(child12)

    em.persist(root)
    em.flush()
    em.clear()

    val node = em.find(classOf[TreeNode], child1.getId)
    assert(node.children.size == 2)
    assert(node.parent == root)

    // JPQL로 로드하기.
    val roots = em.createQuery("select x from TreeNode x where x.parent is null").getResultList
    assert(roots.size() == 1)
    assert(roots.get(0) == root)
  }

}

@Entity
@Access(AccessType.FIELD)
@hba.Cache(region = "tree", usage = CacheConcurrencyStrategy.READ_WRITE)
@hba.DynamicInsert
@hba.DynamicUpdate
class TreeNode extends LongEntity with TreeEntity[TreeNode] {

  var title: String = _
  var data: String = _
  var description: String = _


  @ManyToOne(fetch = FetchType.LAZY)
  @hba.LazyToOne(hba.LazyToOneOption.PROXY)
  @JoinColumn(name = "parentId", nullable = true)
  @BeanProperty
  var parent: TreeNode = _

  @OneToMany(mappedBy = "parent", cascade = Array(CascadeType.ALL), orphanRemoval = true)
  @hba.LazyCollection(hba.LazyCollectionOption.EXTRA)
  @BeanProperty
  val children: util.Set[TreeNode] = new util.HashSet[TreeNode]()

  @Embedded
  @BeanProperty
  var nodePosition = new TreeNodePosition()

  override def addChild(child: TreeNode) {
    child.setParent(this)
    children.add(child)
  }
  override def removeChild(child: TreeNode) {
    require(child != null)
    if (children.remove(child))
      child.setParent(null)
  }


  @inline
  override def hashCode(): Int = Hashs.compute(title)
}

object TreeNode {

  def apply(title: String, parent: TreeNode = null): TreeNode = {
    val node = new TreeNode()
    node.title = title
    node.parent = parent
    node
  }
}
