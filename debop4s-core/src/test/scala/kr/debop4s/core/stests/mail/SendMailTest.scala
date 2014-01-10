package kr.debop4s.core.stests.mail

import javax.mail.internet.{MimeBodyPart, MimeMultipart, InternetAddress, MimeMessage}
import javax.mail.{MessagingException, Transport, Message, Session}
import org.junit.Test
import org.slf4j.LoggerFactory

/**
 * kr.debop4s.core.stests.mail.SendMailTest 
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 1. 10. 오후 4:20
 */
class SendMailTest {

    implicit lazy val log = LoggerFactory.getLogger(classOf[SendMailTest])

    val to = "debop@hconnect.co.kr"
    val from = "noreply@hconnect.co.kr"
    val host = "localhost"
    val properties = System.getProperties
    properties.setProperty("mail.smtp.host", host)

    @Test
    def sendTextMailTest() {

        val session = Session.getDefaultInstance(properties)

        try {
            val message = new MimeMessage(session)
            message.setFrom(new InternetAddress(from))
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to))

            message.setSubject("This is the Subject Line!")
            message.setText("This is actual message\r\n This is second line.")

            // send message
            Transport.send(message)
            System.out.println("Sent message successfully...")
        } catch {
            case mex: MessagingException => System.err.print(mex)
            case e: Exception => System.err.print(e)
        }
    }

    @Test
    def sendHtmlMailTest() {
        val session = Session.getDefaultInstance(properties)

        try {
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
            Transport.send(message)
            System.out.println("Sent message successfully...")
        } catch {
            case mex: MessagingException => System.err.print(mex)
            case e: Exception => System.err.print(e)
        }
    }
}
