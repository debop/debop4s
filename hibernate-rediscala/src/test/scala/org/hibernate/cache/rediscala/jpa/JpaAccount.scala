package org.hibernate.cache.rediscala.jpa

import javax.persistence._

import org.hibernate.cache.rediscala.Hashs

/**
 * JpaAccount 
 *
 * @author sunghyouk.bae@gmail.com
 * @since 2014. 2. 26.
 */
@Entity
@Access(AccessType.FIELD)
@NamedQuery(name = "JpaAccount.findByName", query = "select ja from JpaAccount ja where ja.name=?1")
@SerialVersionUID(8986275418970766284L)
class JpaAccount extends Serializable {

  @Id
  @GeneratedValue
  var id: java.lang.Long = _

  @Column(name = "accountId")
  def getId = id

  var cashBalance: java.lang.Double = _

  @Column(name = "accountName", nullable = false, length = 32)
  var name: String = _

  override def hashCode(): Int = Hashs.compute(name)

}
