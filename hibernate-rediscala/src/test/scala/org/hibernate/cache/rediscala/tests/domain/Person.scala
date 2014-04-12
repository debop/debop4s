package org.hibernate.cache.rediscala.tests.domain

import java.util
import java.util.Objects
import javax.persistence._
import org.hibernate.annotations.{Cascade, CascadeType, CacheConcurrencyStrategy}


@Entity
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Access(AccessType.FIELD)
@SerialVersionUID(-8245742950718661800L)
class Person extends Serializable {

  @Id
  @GeneratedValue
  var id: java.lang.Long = _

  var age: Integer = _
  var firstName: String = _
  var lastName: String = _

  // FST 에서 Float 수형이 제대로 안된다.
  var height: java.lang.Double = 180.8
  var weight: java.lang.Double = 77.7

  @ManyToMany(mappedBy = "participants")
  var events: util.List[Event] = new util.ArrayList[Event]()

  @CollectionTable(name = "EmailAddressSet", joinColumns = Array(new JoinColumn(name = "personId")))
  @ElementCollection(targetClass = classOf[String])
  @Cascade(Array(CascadeType.ALL))
  var emailAddress: util.Set[String] = new util.HashSet[String]()

  @CollectionTable(name = "PhoneNumberSet", joinColumns = Array(new JoinColumn(name = "personId")))
  @ElementCollection(targetClass = classOf[PhoneNumber])
  @Cascade(Array(CascadeType.ALL))
  var phoneNumbers: util.Set[PhoneNumber] = new util.HashSet[PhoneNumber]()

  @CollectionTable(name = "TailsManList", joinColumns = Array(new JoinColumn(name = "personId")))
  @ElementCollection(targetClass = classOf[String])
  @Cascade(Array(CascadeType.ALL))
  var tailsmans: util.List[String] = new util.ArrayList[String]()

  override def equals(obj: Any): Boolean = {
    obj match {
      case x: Person => hashCode == x.hashCode
      case _ => false
    }
  }

  override def hashCode: Int = Objects.hash(firstName, lastName, height, weight)

}
