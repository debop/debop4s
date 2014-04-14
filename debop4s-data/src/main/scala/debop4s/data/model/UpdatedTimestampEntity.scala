package debop4s.data.model

import javax.persistence.PrePersist
import org.joda.time.DateTime

/**
 * 최근 수정일자를 속성으로 가지고 있는 엔티티를 표현하는 인터페이스
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 24. 오후 4:16
 */
trait UpdatedTimestampEntity {

    @org.hibernate.annotations.Type(`type` = "debop4s.data.hibernate.usertype.JodaDateTimeUserType")
    var updatedTimestamp: DateTime = _

    def getUpdatedTimestamp: DateTime = updatedTimestamp

    @PrePersist
    def updateUpdatedTimestamp() {
        updatedTimestamp = DateTime.now()
    }

}
