package com.github.debop4s.core.spring

/**
 * com.github.debop4s.core.spring.Profiles
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 12. 오전 10:54
 */
class Profiles extends Enumeration {

    type Profiles = Value

    val LOCAL = Value(0, "LOCAL")

    val DEVELOP = Value(1, "DEVELOP")

    val TEST = Value(2, "TEST")

    val PRODUCTION = Value(3, "PRODUCTION")
}
