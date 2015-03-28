package debop4s.core.mail

import javax.mail.internet.{ MimeBodyPart, MimeMultipart, InternetAddress, MimeMessage }
import javax.mail.{ MessagingException, Transport, Message, Session }
import org.scalatest.{ BeforeAndAfter, Matchers, FunSuite }
import org.slf4j.LoggerFactory

/**
 * 메일 발송 테스트를 위해서는 OSX에서는 sendemail 을 설치하세요.
 * SMTP 시작 : $sudo postfix start
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 1. 10. 오후 4:20
 */
class SendMailTest extends FunSuite with Matchers with BeforeAndAfter {

  lazy val log = LoggerFactory.getLogger(getClass)

  val to = "debop@hconnect.co.com"
  val from = "noreply@hconnect.co.com"
  val host = "localhost"
  val properties = System.getProperties
  properties.setProperty("mail.smtp.host", host)

  test("send plain text mail") {

    val session = Session.getDefaultInstance(properties)

    val message = new MimeMessage(session)
    message.setFrom(new InternetAddress(from))
    message.addRecipient(Message.RecipientType.TO, new InternetAddress(to))

    message.setSubject("This is the Subject Line!")
    message.setText("This is actual message\r\n This is second line.")

    // send message
    try {
      Transport.send(message)
    } catch {
      case e: MessagingException => log.warn("메일 서버를 설정하세요.")
    }
  }

  test("send html mail") {
    val session = Session.getDefaultInstance(properties)

    val message = new MimeMessage(session)
    message.setFrom(new InternetAddress(from))
    message.addRecipient(Message.RecipientType.TO, new InternetAddress(to))

    message.setSubject("메일 제목입니다.")
    val body = new MimeBodyPart()

    body.setText("<h1>메일 본문 제목입니다.</h1>\n<hr>\n<div>메일 본문이구요.</div>", "UTF-8", "html")

    val multipart = new MimeMultipart()
    multipart.addBodyPart(body)
    message.setContent(multipart)

    // send message
    try {
      Transport.send(message)
    } catch {
      case e: MessagingException => log.warn("메일 서버를 설정하세요.")
    }
  }
}
