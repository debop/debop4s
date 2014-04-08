package debop4s.data.tests.mapping.query

import debop4s.core.utils.Hashs
import debop4s.data.model.HibernateEntity
import debop4s.data.tests.AbstractJpaTest
import javax.persistence._
import org.hibernate.{annotations => hba}
import org.junit.Before
import org.junit.Test
import org.slf4j.LoggerFactory
import org.springframework.transaction.annotation.Transactional


/**
 * Created by debop on 2014. 3. 9.
 */
@Transactional
class QueryTest extends AbstractJpaTest {

  private lazy val log = LoggerFactory.getLogger(getClass)

  @PersistenceContext val em: EntityManager = null

  @Test
  def simpleQueries() {
    val hypothesisName = classOf[Hypothesis].getName

    assertQuery(em, 4, em.createQuery("select h from Hypothesis h"))
    assertQuery(em, 4, em.createQuery(s"select h from $hypothesisName h"))
    assertQuery(em, 1, em.createQuery("select h from Helicopter h"))
  }

  @Test
  def constantParameterQuery() {
    assertQuery(em, 1, em.createQuery("select h from Hypothesis h where h.description = 'stuff works'"))
  }

  @Test
  def parametericQuery() {
    val query = em.createQuery("select h from Hypothesis h where h.description = :myParam")
      .setParameter("myParam", "stuff works")
    assertQuery(em, 1, query)
  }

  def assertQuery(em: EntityManager, expectedSize: Int, testQuery: Query) {
    assert(testQuery.getResultList.size == expectedSize)
    em.clear()
  }

  @Before
  def setup() {
    log.info("예제용 데이터 추가...")

    em.createQuery("delete from Hypothesis").executeUpdate()
    em.createQuery("delete from Helicopter").executeUpdate()

    val socrates = new Hypothesis()
    socrates.id = "13"
    socrates.description = "There are more than two dimensions over the shadows we see out of the cave"
    socrates.position = 1
    em.persist(socrates)

    val peano = new Hypothesis()
    peano.id = "14"
    peano.description = "Peano's curve and then Hilbert's space filling curve proof the connection from mono-dimensional to bi-dimensional space"
    peano.position = 2
    em.persist(peano)

    val sanne = new Hypothesis()
    sanne.id = "15"
    sanne.description = "Hilbert's proof of connection to 2 dimensions can be induced to reason on N dimensions"
    sanne.position = 3
    em.persist(sanne)

    val shortOne = new Hypothesis()
    shortOne.id = "16"
    shortOne.description = "stuff works"
    shortOne.position = 4
    em.persist(shortOne)

    val helicopter = new Helicopter()
    helicopter.name = "No creative clue"
    em.persist(helicopter)

    em.flush()
    em.clear()
  }
}

@Entity
@hba.Cache(region = "simple.query", usage = hba.CacheConcurrencyStrategy.READ_WRITE)
@hba.DynamicInsert
@hba.DynamicUpdate
class Helicopter extends HibernateEntity[java.lang.Long] {

  @Id
  @GeneratedValue
  var id: java.lang.Long = _

  override def getId = id

  var name: String = _

  override def hashCode(): Int = Hashs.compute(name)
}

@Entity
@hba.Cache(region = "simple.query", usage = hba.CacheConcurrencyStrategy.READ_WRITE)
@hba.DynamicInsert
@hba.DynamicUpdate
class Hypothesis extends HibernateEntity[String] {

  @Id
  var id: String = _

  override def getId = id

  var description: String = _
  var position: Integer = _

  override def hashCode(): Int = Hashs.compute(description, position)
}