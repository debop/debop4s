package debop4s.web.scalatra.scalate

import org.apache.commons.codec.binary.Base64
import org.joda.time.DateTime

case class Person(id: String,
                  name: String,
                  email: String,
                  registDate: DateTime = new DateTime()) {

  lazy val encName: String = Base64.encodeBase64URLSafeString(name.getBytes("UTF-8"))
}