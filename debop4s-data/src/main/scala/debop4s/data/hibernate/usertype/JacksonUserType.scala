package debop4s.data.hibernate.usertype

import debop4s.core.json.JacksonSerializer

/**
 * [[JacksonSerializer]] 를 이용하여 객체를 Json 문자열로 저장합니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 24. 오후 5:13
 */
class JacksonUserType extends AbstractJsonUserType {

    override val serializer = JacksonSerializer()

}
