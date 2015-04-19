//package debop4s.data.stest.jpa.mysql
//
//import javax.persistence.EntityManager
//
//import debop4s.data.orm.jpa.repository.JpaDao
//import debop4s.data.orm.jpa.mysql.service.{ScalaJpaEntity2Service, ScalaJpaEntityService}
//import debop4s.data.orm.jpa.{ScalaJpaEntity, ScalaJpaEntity2}
//import org.junit.Test
//import org.junit.runner.RunWith
//import org.scalatest.Matchers
//import org.scalatest.junit.JUnitSuite
//import org.slf4j.LoggerFactory
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
//import org.springframework.test.context.support.AnnotationConfigContextLoader
//import org.springframework.test.context.{ContextConfiguration, TestContextManager}
//
///**
// * MySQL Master-Slaves 환경하에서 ReadOnly 작업이 제대로 되는지 테스트
// * @author sunghyouk.bae@gmail.com 2014. 9. 7.
// */
//@RunWith(classOf[SpringJUnit4ClassRunner])
//@ContextConfiguration(classes = Array(classOf[ScalaJpaMySqlReplicationConfiguration]),
//                       loader = classOf[AnnotationConfigContextLoader])
//class ScalaJpaMySqlReplicationTest extends JUnitSuite with Matchers {
//
//  private val log = LoggerFactory.getLogger(getClass)
//
//  @Autowired val entityService: ScalaJpaEntityService = null
//  @Autowired val entity2Service: ScalaJpaEntity2Service = null
//  @Autowired val dao: JpaDao = null
//  @Autowired val em: EntityManager = null
//
//  // NOTE: TestContextManager#prepareTestInstance 를 실행시켜야 제대로 Dependency Injection이 됩니다.
//  new TestContextManager(this.getClass).prepareTestInstance(this)
//
//  /**
//   * Tx 하에서 ScalaJpaEntity 를 저장하고, 또 다른 ScalaJpaEntity2 를 읽기 전용으로 읽어온다.
//   */
//  @Test
//  def saveEntityAndLoadOtherEntity(): Unit = {
//    // 1. 나중에 읽어오기 위해서 따로 ScalaJpaEntity2 를 미리 저장한다.
//    val entity2 = entity2Service.save(new ScalaJpaEntity2("미리 저장한 것"))
//    log.debug(s"entity2=$entity2")
//
//    val loaded2 = entity2Service.load(entity2.getId)
//    loaded2 should not be null
//    loaded2 shouldEqual entity2
//
//    // 2. ScalaJpaEntity를 저장한다.
//    val entity = new ScalaJpaEntity("신규")
//    entityService.save(entity)
//    log.debug(s"entity=$entity")
//
//    val loaded = entityService.load(entity.getId)
//    log.debug(s"loaded=$loaded")
//    loaded should not be null
//
//    //intercept[EmptyResultDataAccessException] {
//    dao.delete(classOf[ScalaJpaEntity], entity.getId)
//    //}
//  }
//
//  @Test
//  def testReadOnlyWork(): Unit = {
//    log.trace("start readonly work...")
//
//    val entities = entityService.findAll
//    entities should not be null
//
//    log.trace("finish readonly work!!!")
//  }
//
//  @Test
//  def testNestedTransaction(): Unit = {
//    val entity = new ScalaJpaEntity()
//    entity.name = "저장하기"
//
//    // write and read
//    val saved = entityService.saveAndRefresh(entity)
//    log.debug(s"Saved entity=$saved")
//    saved should not be null
//    saved shouldEqual entity
//  }
//
//  @Test
//  def testSaveAndLoad(): Unit = {
//    val entity = new ScalaJpaEntity()
//    entity.name = "저장하기"
//
//    // write
//    val saved = entityService.save(entity)
//    log.debug(s"saved entity=$saved")
//    saved shouldEqual entity
//
//    // read only
//    val loaded = entityService.load(saved.getId)
//    log.debug(s"Loaded entity=$loaded")
//    loaded should not be null
//    loaded shouldEqual entity
//
//    // read only
//    val loaded2 = entityService.load(loaded.getId)
//    log.debug(s"Loaded entity=$loaded2")
//    loaded2 should not be null
//    loaded2 shouldEqual entity
//
//    // read only
//    val loadedByJpql = entityService.loadByJPQL(loaded.getId)
//    log.debug(s"Loaded entity by JPQL=$loadedByJpql")
//    loadedByJpql should not be null
//    loadedByJpql shouldEqual entity
//
//    // write
//    log.trace("save entity repeatly")
//    entity.name = "repeatly"
//    entityService.save(entity)
//
//    // read only
//    val loaded3 = entityService.loadByJPQL(saved.getId)
//    log.debug(s"Loaded entity by JPQL=$loaded3")
//    loaded3 should not be null
//    loaded3 shouldEqual entity
//
//    // read only
//    val loaded4 = entityService.load(saved.getId)
//    log.debug(s"Loaded entity=$loaded4")
//    loaded4 should not be null
//    loaded4 shouldEqual entity
//
//    entityService.save(loaded4)
//  }
//
//  @Test
//  def readOnlyContainsTransactional(): Unit = {
//    val entity = new ScalaJpaEntity()
//    entity.name = "저장하기"
//
//    // write
//    log.trace("save entity repeatly")
//    val saved = entityService.saveWithNewTransaction(entity)
//    log.debug(s"saved=$saved")
//    saved should not be null
//
//    //    val loaded = entityService.loadAndUpdate(saved.getId)
//    //    log.debug(s"Loaded entity=$loaded")
//    //    loaded should not be null
//    //    loaded shouldEqual saved
//
//    val loaded2 = entityService.load(saved.getId)
//    loaded2 should not be null
//    loaded2.name should not eq saved.name
//  }
//}
