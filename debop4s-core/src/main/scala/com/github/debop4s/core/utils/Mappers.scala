package com.github.debop4s.core.utils

import org.modelmapper.ModelMapper
import org.modelmapper.config.Configuration.AccessLevel
import org.modelmapper.convention.MatchingStrategies
import org.slf4j.LoggerFactory
import scala.collection.immutable.IndexedSeq
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.reflect._

/**
 * [[ModelMapper]]를 이용하여, 서로 상이한 수형 간의 정보를 복사할 수 있도록 합니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 12. 오후 2:14
 */
object Mappers {

  private lazy val log = LoggerFactory.getLogger(getClass)

  val mapper = new ModelMapper()

  mapper.getConfiguration
  .setFieldMatchingEnabled(true)
  .setMatchingStrategy(MatchingStrategies.STANDARD)
  .setFieldAccessLevel(AccessLevel.PRIVATE)

  def map[T <: AnyRef](src: AnyRef, dest: T) {
    mapper.map(src, dest)
  }

  def map[T: ClassTag](src: AnyRef) = mapper.map[T](src, classTag[T].runtimeClass)

  def mapAll[T: ClassTag](srcs: Iterable[_]): Seq[T] = {
    val targetClass = classTag[T].runtimeClass
    srcs.toSeq.map(src => mapper.map(src, targetClass).asInstanceOf[T])
  }

  def mapAsync[T <: AnyRef](src: AnyRef, dest: T): Future[Unit] = future {
    map[T](src, dest)
  }

  def mapAsync[T: ClassTag](src: AnyRef): Future[T] = future {
    map[T](src)
  }

  def mapAllAsync[T: ClassTag](srcs: Iterable[_]): Future[Seq[T]] = future {
    mapAll(srcs)
  }

  def mapAllAsParallel[T: ClassTag](srcs: Iterable[_]): IndexedSeq[T] = {
    val targetClass = classTag[T].runtimeClass
    srcs.par.map(src => mapper.map[T](src, targetClass).asInstanceOf[T]).toIndexedSeq
  }
}
