package debop4s.data.orm.hibernate.listener

import debop4s.data.orm.model.PersistentObject
import org.hibernate.event.spi.{PostInsertEvent, PostInsertEventListener, PostLoadEvent, PostLoadEventListener}
import org.hibernate.persister.entity.EntityPersister

class PersistentObjectListener extends PostLoadEventListener with PostInsertEventListener {

  override def onPostLoad(event: PostLoadEvent) {
    event.getEntity match {
      case x: PersistentObject => x.onLoad()
      case _ =>
    }
  }

  // add by Hibernate 4.3.x
  override def requiresPostCommitHanding(persister: EntityPersister): Boolean = false

  override def onPostInsert(event: PostInsertEvent) {
    event.getEntity match {
      case x: PersistentObject => x.onPersist()
      case _ =>
    }
  }

}
