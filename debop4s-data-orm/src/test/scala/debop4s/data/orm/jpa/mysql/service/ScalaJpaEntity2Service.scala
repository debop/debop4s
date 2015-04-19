package debop4s.data.orm.jpa.mysql.service

import java.lang.{Long => JLong}
import java.util
import java.util.Date
import javax.persistence.{EntityManager, EntityManagerFactory, PersistenceContext}

import debop4s.data.orm.jpa.{ScalaJpaEntity2, _}
import debop4s.data.orm.jpa.mysql.repository.ScalaJpaEntity2Repository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * SimpleEntityService
 * @author sunghyouk.bae@gmail.com 2014. 9. 7.
 */
@Service
@Transactional
class ScalaJpaEntity2Service {

  private lazy val log = LoggerFactory.getLogger(getClass)

  @Autowired val emf: EntityManagerFactory = null
  @Autowired val repo: ScalaJpaEntity2Repository = null

  @PersistenceContext val em: EntityManager = null

  def save(entity: ScalaJpaEntity2): ScalaJpaEntity2 = {
    repo.save(entity)
  }

  @Transactional(readOnly = true)
  def load(id: JLong): ScalaJpaEntity2 = {
    repo.findOne(id)
  }

  @Transactional(readOnly = true)
  def loadByJPQL(id: JLong): ScalaJpaEntity2 = {
    val loaded = repo.findById(id)
    println("loaded by JPQL = " + loaded)
    loaded
  }

  @Transactional(readOnly = true)
  def findAll: util.List[ScalaJpaEntity2] = {
    repo.findAll
  }

  def saveAndRefresh(entity: ScalaJpaEntity2): ScalaJpaEntity2 = {
    log.debug(s"save and flush entity. entity=$entity")
    val saved = repo.saveAndFlush(entity)

    log.debug(s"load entity by JPQL")
    loadByJPQL(saved.getId)
  }


  @Transactional(readOnly = true)
  def loadAndUpdate(id: JLong): ScalaJpaEntity2 = {
    val entity = repo.findOne(id)
    require(entity != null)
    entity.name = new Date().getTime.toString
    saveWithNewTransaction(entity)
  }

  def saveWithNewTransaction(entity: ScalaJpaEntity2): ScalaJpaEntity2 = {
    log.info(s"save and flush entity. entity=$entity")
    emf.withNewEntityManager { em =>
      em.save(entity)
    }
  }
}
