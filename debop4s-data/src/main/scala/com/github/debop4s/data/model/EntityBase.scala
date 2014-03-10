package com.github.debop4s.data.model

import javax.persistence._
import org.hibernate.{ annotations => hba }

@MappedSuperclass
@Access(AccessType.FIELD)
@hba.DynamicInsert
@hba.DynamicUpdate
abstract class LongEntity extends HibernateEntity[java.lang.Long] {

  @Id
  @GeneratedValue
  var id: java.lang.Long = _

  override def getId = id

}

@MappedSuperclass
@Access(AccessType.FIELD)
@hba.DynamicInsert
@hba.DynamicUpdate
abstract class IntEntity extends HibernateEntity[java.lang.Integer] {

  @Id
  @GeneratedValue
  var id: java.lang.Integer = _

  override def getId = id

}

@MappedSuperclass
@Access(AccessType.FIELD)
@hba.DynamicInsert
@hba.DynamicUpdate
abstract class UuidEntity extends HibernateEntity[String] {

  @Id
  @GeneratedValue(generator = "uuid")
  @hba.GenericGenerator(name = "uuid", strategy = "uuid2")
  var id: String = _

  override def getId = id
}

@MappedSuperclass
@Access(AccessType.FIELD)
@hba.DynamicInsert
@hba.DynamicUpdate
abstract class StringEntity extends HibernateEntity[String] {

  @Id
  var id: String = _

  override def getId = id

}
