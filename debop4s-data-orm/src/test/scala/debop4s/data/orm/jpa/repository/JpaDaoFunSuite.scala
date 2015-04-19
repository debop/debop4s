package debop4s.data.orm.jpa.repository

import javax.persistence.{EntityManager, PersistenceContext}

import debop4s.data.orm.AbstractJpaJUnitSuite
import debop4s.data.orm.jpa._
import debop4s.data.orm.mapping.simple.ScalaSimpleEntity
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.annotation.Rollback
import org.springframework.transaction.annotation.Transactional

/**
 * `JpaDao` 에 대한 테스트
 */
@Transactional
class JpaDaoFunSuite extends AbstractJpaJUnitSuite {

  @PersistenceContext val em: EntityManager = null
  @Autowired val dao: JpaDao = null

  @Test
  @Transactional
  def testTransactionalReadOnly() {

    // 새로운 EntityManager를 만들어서 저장해야 실제로 저장된다.
    val entity: ScalaSimpleEntity = dao.withNewEntityManager { em =>
      val entity = new ScalaSimpleEntity()
      entity.name = "JpaDao.withTransaction"
      em.save(entity)
    }

    // Trace 로그 상에, 읽기 Context가 나오는지 확인 해야 한다.
    val loaded = dao.findOne(classOf[ScalaSimpleEntity], entity.getId)
    loaded shouldEqual entity

    val loaded2 = dao.findOne(classOf[ScalaSimpleEntity], entity.getId)
    loaded2 shouldEqual entity

    loaded2.description = "중첩하기"
    dao.save(loaded2)
    dao.flush()

    val loaded3 = dao.findOne(classOf[ScalaSimpleEntity], entity.getId)

    loaded3 should not be null
    loaded3.description should not be entity.description

    dao.delete(loaded3)
  }

  @Test
  @Transactional
  @Rollback(false)
  def testTransactionalReadOnly2() {

    val entity = dao.withNewEntityManager { em =>
      val entity = new ScalaSimpleEntity()
      entity.name = "JpaDao.withTransaction"
      em.save(entity)
    }

    // Trace 로그 상에, 읽기 Context가 나오는지 확인 해야 한다.
    val loaded = dao.findOne(classOf[ScalaSimpleEntity], entity.getId)
    loaded shouldEqual entity

    val loaded2 = dao.findOne(classOf[ScalaSimpleEntity], entity.getId)
    loaded2 shouldEqual entity

    loaded2.description = "중첩하기"
    dao.save(loaded2)
    dao.flush()

    val loaded3 = dao.findOne(classOf[ScalaSimpleEntity], entity.getId)

    loaded3.description should not be entity.description
    dao.delete(loaded3)
  }

}
