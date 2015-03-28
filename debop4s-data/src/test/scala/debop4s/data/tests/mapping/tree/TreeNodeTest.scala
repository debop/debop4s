package debop4s.data.tests.mapping.tree

import debop4s.core.utils.Hashs
import debop4s.data.jpa.repository.JpaQueryDslDao
import debop4s.data.model.{ LongEntity, HibernateTreeEntity }
import debop4s.data.tests.AbstractJpaTest
import java.util
import javax.persistence._
import org.hibernate.annotations.CacheConcurrencyStrategy
import org.hibernate.{ annotations => hba }
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Created by debop on 2014. 3. 9.
 */
@Transactional
class TreeNodeTest extends AbstractJpaTest {

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

    val node = em.find(classOf[TreeNode], child1.id)
    assert(node.children.size == 2)
    assert(node.parent == root)

    // JPQL로 로드하기.
    val roots = em.createQuery("select x from TreeNode x where x.parent is null").getResultList
    assert(roots.size() == 1)
    assert(roots.get(0) == root)
  }

}

@Entity
@org.hibernate.annotations.Cache(region = "tree", usage = CacheConcurrencyStrategy.READ_WRITE)
@hba.DynamicInsert
@hba.DynamicUpdate
@Access(AccessType.FIELD)
class TreeNode extends LongEntity with HibernateTreeEntity[TreeNode] {

  var title: String = _
  var data: String = _
  var description: String = _

  override def self: TreeNode = this

  @ManyToOne(fetch = FetchType.LAZY)
  @hba.LazyToOne(hba.LazyToOneOption.PROXY)
  @JoinColumn(name = "parentId", nullable = true)
  var parent: TreeNode = _

  override def getParent: TreeNode = parent

  override def setParent(parent: TreeNode) {
    this.parent = parent
  }

  @OneToMany(mappedBy = "parent", cascade = Array(CascadeType.ALL), orphanRemoval = true)
  @hba.LazyCollection(hba.LazyCollectionOption.EXTRA)
  override val children: util.Set[TreeNode] = new util.HashSet[TreeNode]()

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
