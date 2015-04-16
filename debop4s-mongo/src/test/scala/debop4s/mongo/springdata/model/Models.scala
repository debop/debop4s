package debop4s.mongo.springdata.model

import java.util
import java.util.regex.Pattern

import debop4s.core.utils.{Hashs, Strings}
import debop4s.core.{ToStringHelper, ValueObject}
import debop4s.mongo.AbstractMongoDocument
import org.slf4j.LoggerFactory
import org.springframework.core.convert.converter.Converter
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.{Document, Field}
import org.springframework.stereotype.Component

case class Address(var street: String, var city: String, var country: String)

class EmailAddress extends ValueObject {

  def this(emailAddr: String) {
    this()
    this.value = emailAddr
  }

  val EMAIL_REGEX = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
  val PATTERN = Pattern.compile(EMAIL_REGEX)

  @Field("email")
  var value: String = _

  override def hashCode: Int = Hashs.compute(value)

  override protected def buildStringHelper: ToStringHelper =
    super.buildStringHelper.add("value", value)
}

@Document
@SerialVersionUID(-1040973481509269008L)
class Customer extends AbstractMongoDocument {

  def this(firstname: String, lastname: String) {
    this()
    this.firstname = firstname
    this.lastname = lastname
  }

  var firstname: String = _
  var lastname: String = _

  @Field("email")
  @Indexed(unique = true)
  var emailAddress: EmailAddress = _

  val addresses = new util.HashSet[Address]()

  def add(address: Address): Unit = {
    assert(address != null)
    addresses.add(address)
  }
  override protected def buildStringHelper: ToStringHelper =
    super.buildStringHelper
    .add("firstname", firstname)
    .add("lastname", lastname)
    .add("emailAddress", emailAddress)
}

@Component
class EmailAddressToStringConverter extends Converter[EmailAddress, String] {
  private val log = LoggerFactory.getLogger(getClass)
  override def convert(source: EmailAddress): String = {
    log.debug(s"convert EmailAddress. source=$source")
    if (source == null) null else source.value
  }
}

@Component
class StringToEmailAddressConverter extends Converter[String, EmailAddress] {
  private val log = LoggerFactory.getLogger(getClass)
  override def convert(source: String): EmailAddress = {
    log.debug(s"convert EmailAddress. source=$source")
    if (Strings.isEmpty(source)) null else new EmailAddress(source)
  }
}