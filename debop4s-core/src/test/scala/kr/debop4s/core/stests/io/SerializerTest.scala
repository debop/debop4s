package kr.debop4s.core.stests.io

import kr.debop4s.core.compress.{Compressor, DeflateCompressor, GZipCompressor}
import kr.debop4s.core.cryptography.{TripleDESEncryptor, DESEncryptor, RC2Encryptor, SymmetricEncryptor}
import kr.debop4s.core.io.{EncryptableSerializer, CompressableSerializer, BinarySerializer, Serializer}
import kr.debop4s.core.stests.io.model.{User, Company}
import org.scalatest.{BeforeAndAfter, Matchers, FunSuite}
import org.slf4j.LoggerFactory

/**
 * kr.debop4s.core.tests.io.SerializerTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 15. 오후 8:52
 */
class SerializerTest extends FunSuite with Matchers with BeforeAndAfter {

  lazy val log = LoggerFactory.getLogger(getClass)

  var company: Company = _

  before {
    company = new Company()
    company.code = "HCT"
    company.name = "HealthConnect"
    company.amount = 10000L
    company.description = "헬스커넥트"

    for (i <- 0 until 100) {
      val user = new User()
      user.name = "USER_" + i
      user.empNo = "EMPNO_" + i
      user.address = "ADDR_" + i
      company.users += user
    }
  }


  test("comparessable serialize") {
    val compressors = Array[Compressor](new GZipCompressor(), new DeflateCompressor())
    val serializers = Array[Serializer](new BinarySerializer())

    compressors.foreach(compressor => {
      serializers.foreach(serializer => {
        val cs = new CompressableSerializer(serializer, compressor)
        log.debug(s"encryptor=[${compressor.getClass}], serializer=[${serializer.getClass}]")

        val bytes = cs.serialize(company)
        val deserialized = cs.deserialize(bytes, classOf[Company])

        assert(deserialized != null)
        assert(deserialized.code == company.code)
        assert(deserialized.users.size == company.users.size)
      })
    })
  }

  test("encryptable serialize") {
    val encryptors = Array[SymmetricEncryptor](new RC2Encryptor(), new DESEncryptor(), new TripleDESEncryptor())
    val serializers = Array[Serializer](new BinarySerializer())

    encryptors.foreach(encryptor => {
      serializers.foreach(serializer => {
        val es = new EncryptableSerializer(serializer, encryptor)
        log.debug(s"encryptor=[${encryptor.getClass}], serializer=[${serializer.getClass}]")

        val bytes = es.serialize(company)
        val deserialized = es.deserialize(bytes, classOf[Company])

        assert(deserialized != null)
        assert(deserialized.code == company.code)
        assert(deserialized.users.size == company.users.size)
      })
    })

  }

}
