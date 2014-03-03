package com.github.debop4s.redis.spring

import com.github.debop4s.redis.model.User
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.{CacheEvict, Cacheable}
import org.springframework.stereotype.Repository


/**
 * com.github.debop4s.redis.spring.UserRepository 
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 24. 오전 11:00
 */
@Repository
class UserRepository {

  private lazy val log = LoggerFactory.getLogger(getClass)

  @Cacheable(value = Array("user"), key = "'user'.concat(':').concat(#id)")
  def getUser(id: String, favoriteMovieSize: Int = 1000): User = {
    log.info(s"새로운 사용자를 생성합니다. id=$id")

    val user = User(favoriteMovieSize)
    user.setId(id)

    user
  }

  /**
  * id 값은 Java의 getter 가 있어야만 합니다.
  */
  @CacheEvict(value = Array("user"), key = "'user'.concat(':').concat(#user.id)")
  def updateUser(user: User) {
    log.info("사용자 정보를 갱신합니다. 캐시는 삭제됩니다...")
  }

}
