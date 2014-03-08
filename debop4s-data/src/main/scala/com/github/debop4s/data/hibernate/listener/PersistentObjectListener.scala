package com.github.debop4s.data.hibernate.listener

import com.github.debop4s.data.model.PersistentObject
import org.hibernate.event.spi.{PostLoadEvent, PostInsertEvent, PostInsertEventListener, PostLoadEventListener}
import org.hibernate.persister.entity.EntityPersister

/**
 * com.github.debop4s.data.hibernate.listener.PersistentObjectListener 
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 25. 오후 5:07
 */
class PersistentObjectListener extends PostLoadEventListener with PostInsertEventListener {

  override def onPostLoad(event: PostLoadEvent) {
    event.getEntity match {
      case x: PersistentObject => x.onLoad()
      case _ =>
    }
  }

  override def requiresPostCommitHanding(persister: EntityPersister): Boolean = false

  override def onPostInsert(event: PostInsertEvent) {
    event.getEntity match {
      case x: PersistentObject => x.onPersist()
      case _ =>
    }
  }
}
