package com.github.debop4s.data.model

import javax.persistence.{MappedSuperclass, Id, GeneratedValue}

/**
 * Created by debop on 2014. 3. 8.
 */
@MappedSuperclass
abstract class LongEntity extends HibernateEntity[java.lang.Long] {

  @Id
  @GeneratedValue
  var id: java.lang.Long = _

  def getId = id

}
