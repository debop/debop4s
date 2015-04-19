package debop4s.data.orm.jpa.mysql.service

import java.lang.{Long => JLong}
import java.util
import java.util.Date
import javax.persistence.{EntityManager, EntityManagerFactory, PersistenceContext}

import debop4s.data.orm.jpa.{ScalaJpaEntity, _}
import debop4s.data.orm.jpa.mysql.repository.ScalaJpaEntityRepository
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
class ScalaJpaEntityService {

  private lazy val log = LoggerFactory.getLogger(getClass)

  @Autowired val emf: EntityManagerFactory = null
  @Autowired val repo: ScalaJpaEntityRepository = null
  @PersistenceContext val em: EntityManager = null

  def save(entity: ScalaJpaEntity): ScalaJpaEntity = {
    repo.save(entity)
  }

  @Transactional(readOnly = true)
  def load(id: JLong): ScalaJpaEntity = {
    repo.findOne(id)
  }

  @Transactional(readOnly = true)
  def loadByJPQL(id: JLong): ScalaJpaEntity = {
    repo.findById(id)
  }

  @Transactional(readOnly = true)
  def findAll: util.List[ScalaJpaEntity] = {
    repo.findAll
  }

  def saveAndRefresh(entity: ScalaJpaEntity): ScalaJpaEntity = {
    log.debug(s"save and flush entity. entity=$entity")
    val saved = repo.saveAndFlush(entity)

    log.debug(s"load entity by JPQL")
    loadByJPQL(saved.getId)
  }

  @Transactional(readOnly = true)
  def loadAndUpdate(id: JLong): ScalaJpaEntity = {
    val entity = repo.findOne(id)
    require(entity != null)
    entity.name = new Date().getTime.toString
    saveWithNewTransaction(entity)
  }

  /**
   * AutoCommit 기본 값이 false 이므로, 같은 Session에서 save 호출하면 아직 DB에 적용되지 않는다.
   * @param entity
   * @return
   */
  def saveWithNewTransaction(entity: ScalaJpaEntity): ScalaJpaEntity = {

    log.info(s"save and flush entity. entity=$entity")
    emf.withNewEntityManager { em =>
      em.save(entity)
    }
  }
}
