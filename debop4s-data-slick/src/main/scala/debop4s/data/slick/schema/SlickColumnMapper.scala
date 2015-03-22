package debop4s.data.slick.schema

import java.sql.Timestamp

import debop4s.core.cryptography.RC2Encryptor
import debop4s.data.slick.customtypes.EncryptedString
import org.joda.time.DateTime

/**
 * SlickColumnMapper
 * @author sunghyouk.bae@gmail.com 15. 3. 22.
 */
trait SlickColumnMapper {
  this: SlickProfile =>

  import driver.simple._

  implicit def jodaDateTimeType: BaseColumnType[DateTime] =
    MappedColumnType.base[DateTime, Timestamp](dt => new Timestamp(dt.getMillis),
                                                timestamp => new DateTime(timestamp.getTime))

  lazy val encryptor = new RC2Encryptor

  implicit def encryptedStringType: BaseColumnType[EncryptedString] =
    MappedColumnType.base[EncryptedString, String](
                                                    enc => encryptor.encrypt(enc.text),
                                                    cipherText => EncryptedString(encryptor.decrypt(cipherText))
                                                  )

}
