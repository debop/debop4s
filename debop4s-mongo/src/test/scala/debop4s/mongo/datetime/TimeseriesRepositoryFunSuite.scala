package debop4s.mongo.datetime

import debop4s.mongo.AbstractMongoFunSuite
import org.joda.time.DateTime
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.support.AnnotationConfigContextLoader
import org.springframework.test.context.{ContextConfiguration, TestContextManager}

/**
 * TimeseriesRepositoryFunSuite
 * @author sunghyouk.bae@gmail.com
 */
@ContextConfiguration(classes = Array(classOf[TimeseriesConfiguration]),
  loader = classOf[AnnotationConfigContextLoader])
class TimeseriesRepositoryFunSuite extends AbstractMongoFunSuite {

  @Autowired val timeseriesRepo: TimeseriesRepository = null

  override def beforeAll() {
    // NOTE: TestContextManager#prepareTestInstance 를 실행시켜야 제대로 Dependency Injection이 됩니다.
    new TestContextManager(this.getClass).prepareTestInstance(this)
  }

  before {
    timeseriesRepo.deleteAll()
  }

  test("Timeseries 저장") {
    val ts = new Timeseries(DateTime.now, 100)
    val saved = timeseriesRepo.save(ts)
    saved should not be null
  }

  test("Timeseries - Load by timetext") {

    val ts = new Timeseries(DateTime.now, 100)
    val saved = timeseriesRepo.save(ts)

    saved.getId should not be null
    log.debug(s"saved id=${ saved.getId }")

    val text = saved.time.timetext
    val tss = timeseriesRepo.findByTimeTimetext(text)
    tss.size shouldEqual 1

  }
}

