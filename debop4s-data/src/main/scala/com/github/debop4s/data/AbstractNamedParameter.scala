package com.github.debop4s.data

import com.github.debop4s.core.ValueObject
import com.github.debop4s.core.utils.Hashs

/**
 * Query 등에서 사용할 Named Parameter의 기본 클래스
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 19. 오후 9:44
 */
@SerialVersionUID(-5640275306788648337L)
abstract class AbstractNamedParameter(val name: String,
                                      val value: Any) extends ValueObject {

    override def hashCode(): Int =
        Hashs.compute(name)

    override protected def buildStringHelper =
        super.buildStringHelper
        .add("name", name)
        .add("value", value)
}
