package debop4s.core.cryptography

import debop4s.core.AbstractCoreTest
import org.springframework.context.annotation.{ Bean, Configuration }

/**
 * debop4s.core.stests.cryptography.CryptographyConfiguration
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 25. 오전 10:28
 */
@Configuration
class CryptographyConfiguration extends AbstractCoreTest {

  @Bean def md5: MD5StringDigester = new MD5StringDigester
  @Bean def sha1: SHA1StringDigester = new SHA1StringDigester
  @Bean def sha256: SHA256StringDigester = new SHA256StringDigester
  @Bean def sha384: SHA384StringDigester = new SHA384StringDigester
  @Bean def sha512: SHA512StringDigester = new SHA512StringDigester

  @Bean def des = new DESEncryptor
  @Bean def rc2 = new RC2Encryptor
  @Bean def tripleDES = new TripleDESEncryptor

}
