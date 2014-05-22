package debop4s.core.spring

import org.scalatest.{Matchers, FunSuite}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.{Bean, Configuration}
import org.springframework.scheduling.annotation.{Scheduled, EnableScheduling}
import org.springframework.test.context.support.AnnotationConfigContextLoader
import org.springframework.test.context.{TestContextManager, ContextConfiguration}

/**
 * ScheduledTest
 * @author debop created at 2014. 5. 8.
 */
@ContextConfiguration(classes = Array(classOf[ScheduledConfiguration]), loader = classOf[AnnotationConfigContextLoader])
class ScheduledTest extends FunSuite with Matchers {

    @Autowired val scheduledJob: ScheduledJob = null

    // NOTE: TestContextManager#prepareTestInstance 를 실행시켜야 제대로 Dependency Injection이 됩니다.
    new TestContextManager(this.getClass).prepareTestInstance(this)

    test("Scheduled Job") {
        scheduledJob should not be null
        Thread.sleep(1000)

        println("test job")

        Thread.sleep(1000)

        println("finish job")


    }

}

class ScheduledJob {

    @Scheduled(fixedRate = 1000L)
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
