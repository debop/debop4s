package debop4s.shiro.redis

import debop4s.rediscala.client.RedisSyncClient
import debop4s.shiro.AbstractShiroFunSuite
import org.apache.shiro.SecurityUtils
import org.apache.shiro.authc.UsernamePasswordToken
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.support.AnnotationConfigContextLoader
import org.springframework.test.context.{ContextConfiguration, TestContextManager}

/**
 * RedisSessionRepositoryFunSuite
 * @author sunghyouk.bae@gmail.com
 */
@ContextConfiguration(classes = Array(classOf[ShiroRedisConfig]), loader = classOf[AnnotationConfigContextLoader])
class RedisSessionRepositoryFunSuite extends AbstractShiroFunSuite {

  @Autowired val redis: RedisSyncClient = null
  @Autowired val securityManager: org.apache.shiro.mgt.SecurityManager = null

  // NOTE: Spring Autowired 를 수행합니다.
  new TestContextManager(this.getClass).prepareTestInstance(this)

  test("configuration test") {
    securityManager should not be null
  }

  test("logging test") {

    try {

      val subject = SecurityUtils.getSubject

      val token = new UsernamePasswordToken("debop", "@real21")
      token.setRememberMe(true)
      subject.login(token)

      log.info(s"User successfully logged in.")
    }
    finally {
      val subject = SecurityUtils.getSubject
      if (subject != null)
        subject.logout()
    }
  }

}
