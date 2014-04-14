package debop4s.core.springscala

import debop4s.core.compress.{DeflateCompressor, GZipCompressor}
import org.scalatest.FunSuite
import org.slf4j.LoggerFactory
import org.springframework.scala.context.function.{FunctionalConfigApplicationContext, FunctionalConfiguration}

/**
 * Spring-Scala 를 이용하여 Spring Configuration을 수행했지만, 좋아보이지는 않는다.
 * 기존 방식이 크게 어려움이 없기 때문에 그냥 사용하기로 했다.
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 12. 오전 3:19
 */
class FunctionalConfigurationTest extends FunSuite {

    lazy val log = LoggerFactory.getLogger(getClass)

    val context = FunctionalConfigApplicationContext(classOf[CompressorConfiguration])

    val gzip = context.getBean(classOf[GZipCompressor])
    val deflate = context.getBean(classOf[DeflateCompressor])

    test("compressor injection") {
        log.debug("names=" + context.getBeanDefinitionNames.mkString(","))

        assert(gzip != null)
        assert(deflate != null)
    }
}

class CompressorConfiguration extends FunctionalConfiguration {

    bean() {
        new GZipCompressor()
    }

    bean() {
        new DeflateCompressor()
    }
}