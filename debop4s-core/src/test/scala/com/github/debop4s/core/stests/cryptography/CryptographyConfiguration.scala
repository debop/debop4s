package com.github.debop4s.core.stests.cryptography

import com.github.debop4s.core.cryptography.StringDigester
import com.github.debop4s.core.stests.AbstractCoreTest
import org.springframework.context.annotation.{ComponentScan, Configuration}

/**
 * com.github.debop4s.core.stests.cryptography.CryptographyConfiguration 
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 25. 오전 10:28
 */
@Configuration
@ComponentScan(basePackageClasses = Array(classOf[StringDigester]))
class CryptographyConfiguration extends AbstractCoreTest {


}
