package com.github.debop4s.data.tests.jpa.config

import com.github.debop4s.core.utils.{ToStringHelper, Hashs}
import com.github.debop4s.data.model.HibernateEntity
import javax.persistence._

/**
 * ScalaJpaEntity 
 *
 * @author sunghyouk.bae@gmail.com
 * @since 2014. 2. 28.
 */
@Entity
@NamedQuery(name = "ScalaJapEntity.findByName", query = "select x from ScalaJpaEntity x where x.name = ?1")
@Access(AccessType.FIELD)
class ScalaJpaEntity extends HibernateEntity[Long] {

  @Id
  @GeneratedValue
  val id: Long = 0

  override def getId: Long = id

  @Column(name = "entityName", nullable = false, length = 32)
  var name: String = _

  override def hashCode(): Int = Hashs.compute(name)

  override protected def buildStringHelper: ToStringHelper =
    super.buildStringHelper
    .add("name", name)
}
