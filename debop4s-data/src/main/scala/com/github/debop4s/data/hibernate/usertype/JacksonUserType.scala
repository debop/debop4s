package com.github.debop4s.data.hibernate.usertype

import com.github.debop4s.core.json.{JacksonSerializer, JsonSerializer}

/**
 * [[JacksonSerializer]] 를 이용하여 객체를 Json 문자열로 저장합니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 24. 오후 5:13
 */
class JacksonUserType extends AbstractJsonUserType {

    private lazy val serializer = JacksonSerializer()

    override def jsonSerializer: JsonSerializer = serializer

}
