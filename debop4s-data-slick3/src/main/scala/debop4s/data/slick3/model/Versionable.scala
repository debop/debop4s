package debop4s.data.slick3.model

import com.sun.corba.se.spi.ior.IdentifiableFactory

/**
 * 버전 관리를 할 수 있는 엔티티의 기본 Trait
 * @author sunghyouk.bae@gmail.com
 */
trait Versionable extends Identifiable {
  //  def version: Long
  //  def version_=(newVersion: Long)
  //  def withVersion(version: Long): this.type = {
  //    this.version = version
  //    this
  //  }
}
