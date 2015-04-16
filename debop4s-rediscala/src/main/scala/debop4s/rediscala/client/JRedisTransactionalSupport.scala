package debop4s.rediscala.client

import java.lang.{Double => JDouble, Iterable => JIterable, Long => JLong}
import java.util.{List => JList, Map => JMap, Set => JSet}

import debop4s.core.JAction1
import redis.MultiBulkConverter
import redis.commands.{TransactionBuilder, Transactions}

import scala.annotation.varargs
import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * JRedisTransactionalSupport
 * @author sunghyouk.bae@gmail.com
 */
trait JRedisTransactionalSupport {
  this: JRedisSupport =>

  def transactionalRedis: Transactions

  @varargs
  def watch(keys: String*): TransactionBuilder = transactionalRedis.watch(keys: _*)

  def unwatch(tx: TransactionBuilder): Unit = tx.unwatch()

  def withTransaction(block: JAction1[TransactionBuilder]): Future[JList[String]] = {
    val tx = transactionalRedis.transaction()
    block.perform(tx)

    for {reply <- tx.exec()}
      yield MultiBulkConverter.toSeqString(reply).asJava
    //    async {
    //      val reply = await(tx.exec())
    //      MultiBulkConverter.toSeqString(reply).asJava
    //    }
  }
}

