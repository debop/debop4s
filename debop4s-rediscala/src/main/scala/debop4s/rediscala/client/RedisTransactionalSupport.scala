package debop4s.rediscala.client

import redis.MultiBulkConverter
import redis.commands.{TransactionBuilder, Transactions}

import scala.annotation.varargs
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * RedisSyncTransactionSupport
 * @author sunghyouk.bae@gmail.com
 */
trait RedisTransactionalSupport {
  this: RedisSynchronizedSupport =>

  def redisTx: Transactions

  @varargs
  def watch(keys: String*): TransactionBuilder = redisTx.watch(keys: _*)

  def unwatch(tx: TransactionBuilder) = tx.unwatch()

  def transaction(): TransactionBuilder = redisTx.transaction()

  def withTransaction(block: TransactionBuilder => Unit): Future[Seq[String]] = {
    val tx = redisTx.transaction()
    block(tx)

    for {reply <- tx.exec()}
      yield MultiBulkConverter.toSeqString(reply)
    //    async {
    //      val reply = await(tx.exec())
    //      MultiBulkConverter.toSeqString(reply)
    //    }
  }

}
