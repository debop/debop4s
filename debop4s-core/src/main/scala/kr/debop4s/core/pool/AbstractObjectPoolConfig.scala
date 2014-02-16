package kr.debop4s.core.pool

import org.apache.commons.pool.impl.GenericObjectPool

/**
 * kr.debop4s.core.pool.AbstractPoolConfig
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 11. 오후 4:36
 */
trait AbstractObjectPoolConfig extends GenericObjectPool.Config {

  def getMaxIdle: Int = maxIdle

  def setMaxIdle(value: Int) { maxIdle = value }

  def getMaxActive: Int = maxActive

  def setMaxActive(value: Int) { maxActive = value }

  def getWhenExhaustedAction: Byte = whenExhaustedAction

  def setWhenExhaustedAction(value: Byte) { whenExhaustedAction = value }

  def isTestOnBorrow: Boolean = testOnBorrow

  def setTestOnBorrow(value: Boolean) { testOnBorrow = value }

  def isTestOnReturn: Boolean = testOnReturn

  def setTestOnReturn(value: Boolean) { testOnReturn = value }

  def isTestWhileIdle: Boolean = testWhileIdle

  def setTestWhileIdle(value: Boolean) { testWhileIdle = value }

  def getTimeBetweenEvictionRunsMillis: Long = timeBetweenEvictionRunsMillis

  def setTimeBetweenEvictionRunsMillis(value: Long) { timeBetweenEvictionRunsMillis = value }

  def getNumTestsPerEvictionRun: Int = numTestsPerEvictionRun

  def setNumTestsPerEvictionRun(value: Int) { numTestsPerEvictionRun = value }

  def getMinEvictableIdleTimeMillis: Long = minEvictableIdleTimeMillis

  def setMinEvictableIdleTimeMillis(value: Long) { minEvictableIdleTimeMillis = value }

  def getSoftMinEvictableIdleTimeMillis: Long = softMinEvictableIdleTimeMillis

  def setSoftMinEvictableIdleTimeMillis(value: Long) { softMinEvictableIdleTimeMillis = value }

}
