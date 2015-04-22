package debop4s.core.utils

import java.io.{File, IOException}
import java.util

import scala.collection.JavaConverters._
import scala.io.Source
import scala.util.parsing.combinator._

/**
 * 텍스트 파일로부터 인증 정보를 읽어오기 위한
 */
object Credentials {

  object parser extends RegexParsers {

    override val whiteSpace = "(?:\\s+|#.*\\n)+".r
    val token = "[\\w-_]+".r

    def auth: parser.Parser[(String, String)] = (token <~ ":") ~ "[^\\n]+".r ^^ { case k ~ v => (k, v) }
    def content: Parser[Map[String, String]] = rep(auth) ^^ { auths => Map(auths: _*) }

    def apply(in: String): Map[String, String] = {
      parseAll(content, in) match {
        case Success(result, _) => result
        case x: Failure => throw new IOException(x.toString())
        case x: Error => throw new IOException(x.toString())
      }
    }
  }

  def apply(file: File): Map[String, String] = parser(Source.fromFile(file).mkString)
  def apply(data: String): Map[String, String] = parser(data)
  def byName(name: String): Map[String, String] =
    apply(new File(System.getenv().asScala.getOrElse("KEY_FOLDER", "/etc/keys")))
}


/**
 * Java interface to Credentials
 */
class Credentials {
  def read(data: String): util.Map[String, String] = Credentials(data).asJava
  def read(file: File): util.Map[String, String] = Credentials(file).asJava
  def byName(name: String): util.Map[String, String] = Credentials.byName(name).asJava
}
