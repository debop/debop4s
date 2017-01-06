package debop4s.core.io

import debop4s.core.AbstractCoreFunSuite
import debop4s.core.compress._
import debop4s.core.cryptography._
import debop4s.core.io.model.{Company, User}

/**
 * SerializerTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 15. 오후 8:52
 */
class SerializerTest extends AbstractCoreFunSuite {

  val serializers = Array[Serializer](new BinarySerializer(), new FstSerializer())

  var company: Company = _

  before {
    company = new Company()
    company.code = "GOOGLE"
    company.name = "Google"
    company.amount = 10000L
    company.description = "구글"

    for (i <- 0 until 100) {
      val user = new User()
      user.name = "USER_" + i
      user.empNo = "EMPNO_" + i
      user.address = "ADDR_" + i
      company.users.add(user)
    }
  }


  test("comparessable serialize") {
    val compressors = Array[Compressor](new GZipCompressor(), new DeflateCompressor(), new SnappyCompressor())

    compressors.foreach { compressor =>
      serializers.foreach { serializer =>
        val cs = new CompressableSerializer(serializer, compressor)
        log.debug(s"encryptor=[${ compressor.getClass }], serializer=[${ serializer.getClass }]")

        val bytes = cs.serialize(company)
        val deserialized = cs.deserialize(bytes, classOf[Company])

        assert(deserialized != null)
        assert(deserialized.code == company.code)
        assert(deserialized.users.size == company.users.size)
      }
    }
  }

  test("encryptable serialize") {
    val encryptors = Array[SymmetricEncryptorSupport](new RC2Encryptor(), new DESEncryptor(), new TripleDESEncryptor())

    encryptors.foreach { encryptor =>
      serializers foreach { serializer =>
        val es = new EncryptableSerializer(serializer, encryptor)
        log.debug(s"encryptor=[${encryptor.getClass}], serializer=[${serializer.getClass}]")

        val bytes = es.serialize(company)
        val deserialized = es.deserialize(bytes, classOf[Company])

        assert(deserialized != null)
        assert(deserialized.code == company.code)
        assert(deserialized.users.size == company.users.size)
      }
    }

  }

}
