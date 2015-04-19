package debop4s.data.orm.hibernate.listener

import debop4s.data.orm.model.UpdatedTimestampEntity
import org.hibernate.event.spi.{PreInsertEvent, PreInsertEventListener, PreUpdateEvent, PreUpdateEventListener}

/**
 * [[UpdatedTimestampEntity]] 를 구현한 엔티티의 최신 갱신 시각을 갱신해주는 리스너입니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 25. 오후 5:02
 */
class UpdatedTimestampListener extends PreInsertEventListener with PreUpdateEventListener {

  override def onPreInsert(event: PreInsertEvent): Boolean = {
    event.getEntity match {
      case x: UpdatedTimestampEntity =>
        x.updateUpdatedTimestamp()
        true
      case _ => false
    }
  }

  override def onPreUpdate(event: PreUpdateEvent): Boolean = {
    event.getEntity match {
      case x: UpdatedTimestampEntity =>
        x.updateUpdatedTimestamp()
        true
      case _ => false
    }
  }
}
