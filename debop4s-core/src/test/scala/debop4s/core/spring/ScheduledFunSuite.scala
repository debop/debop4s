package debop4s.core.spring

import debop4s.core.AbstractCoreFunSuite
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.{Bean, Configuration}
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.support.AnnotationConfigContextLoader
import org.springframework.test.context.{ContextConfiguration, TestContextManager}

/**
 * ScheduledFunSuite
 * @author debop created at 2014. 5. 8.
 */
@RunWith(classOf[SpringJUnit4ClassRunner])
@ContextConfiguration(classes = Array(classOf[ScheduledConfiguration]), loader = classOf[AnnotationConfigContextLoader])
class ScheduledFunSuite extends AbstractCoreFunSuite {

  @Autowired val scheduledJob: ScheduledJob = null

  // Spring Autowired 를 수행합니다.
  new TestContextManager(this.getClass).prepareTestInstance(this)

  test("scheduled job") {
    scheduledJob should not be null
    Thread.sleep(1000)

    log.debug("test job")

    Thread.sleep(1000)

    log.debug("finish job")
  }

}

class ScheduledJob {

  // @Scheduled(fixedRate = 1000L)
  def task() {
    println("스케쥴 작업입니다.")
  }
}

@Configuration
@EnableScheduling
class ScheduledConfiguration {

  @Bean
  def scheduledJob() = new ScheduledJob()
}
