package org.hibernate.cache.rediscala.client

import redis.commands.{TransactionBuilder, Transactions}
import redis.protocol.MultiBulk

import scala.concurrent.Future

/**
 * RedisTransactionSupport
 * @author sunghyouk.bae@gmail.com
 */
trait RedisTransactionSupport {

  def transactionalRedis: Transactions

  /**
   * execute `block` with reids transaction
   * @param block code block to execute
   */
  def withTransaction(block: TransactionBuilder => Unit): Future[MultiBulk] = {
    require(block != null)

    val tx = transactionalRedis.transaction()
    block(tx)
    tx.exec()
  }
}
