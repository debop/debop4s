package kr.debop4s.core.spring

/**
 * kr.debop4s.core.spring.Profiles
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 12. 오전 10:54
 */
class Profiles extends Enumeration {

    type Profiles = Value

    val LOCAL = Value("LOCAL")

    val DEVELOP = Value("DEVELOP")

    val TEST = Value("TEST")

    val PRODUCTION = Value("PRODUCTION")
}
