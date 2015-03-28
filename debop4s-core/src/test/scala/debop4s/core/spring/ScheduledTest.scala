package debop4s.core.spring

import org.junit.Test
import org.junit.runner.RunWith
import org.scalatest.Matchers
import org.scalatest.junit.JUnitSuite
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.{ Bean, Configuration }
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.support.AnnotationConfigContextLoader

/**
 * ScheduledTest
 * @author debop created at 2014. 5. 8.
 */
@RunWith(classOf[SpringJUnit4ClassRunner])
@ContextConfiguration(classes = Array(classOf[ScheduledConfiguration]), loader = classOf[AnnotationConfigContextLoader])
class ScheduledTest extends JUnitSuite with Matchers {

  @Autowired val scheduledJob: ScheduledJob = null

  @Test
  def scheduleJobTest() {
    scheduledJob should not be null
    Thread.sleep(1000)

    println("test job")

    Thread.sleep(1000)

    println("finish job")
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
