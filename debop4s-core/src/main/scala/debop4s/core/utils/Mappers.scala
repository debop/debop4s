package debop4s.core.utils

import org.modelmapper.ModelMapper
import org.modelmapper.config.Configuration.AccessLevel
import org.modelmapper.convention.MatchingStrategies

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.reflect._
import scala.util.Try

/**
 * `ModelMapper`를 이용하여, 서로 상이한 수형 간의 정보를 복사할 수 있도록 합니다.
 *
 * NOTE: 이 것은 Java 객체에 대해서는 잘 되지만, scala 객체에서는 되지 않는다.
 * Scala 에서는 이 방법보다. implict 변환 함수를 이용하는 것이 낫다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 12. 오후 2:14
 */
object Mappers {

  lazy val mapper: ModelMapper = {
    val mp = new ModelMapper()
    mp.getConfiguration
    .setFieldMatchingEnabled(true)
    .setMatchingStrategy(MatchingStrategies.STANDARD)
    .setFieldAccessLevel(AccessLevel.PRIVATE)
    mp
  }


  def map[@miniboxed T <: AnyRef](src: Any, dest: T): Unit =
    mapper.map(src, dest)

  def map[@miniboxed T: ClassTag](src: Any): T =
    mapper.map[T](src, classTag[T].runtimeClass)

  def mapAll[@miniboxed T: ClassTag](srcs: Iterable[_]): Seq[T] = {
    val targetClass = classTag[T].runtimeClass
    srcs.toSeq.map(src => mapper.map(src, targetClass).asInstanceOf[T])
  }

  def mapAsync[@miniboxed T <: AnyRef](src: Any, dest: T): Future[Unit] = Future {
    map[T](src, dest)
  }

  def mapAsync[@miniboxed T: ClassTag](src: Any): Future[T] = Future {
    map[T](src)
  }

  def mapAllAsync[@miniboxed T: ClassTag](srcs: Iterable[_]): Future[Seq[T]] = Future {
    mapAll(srcs)
  }

  def mapAllAsParallel[@miniboxed T: ClassTag](srcs: Iterable[_]): Iterable[T] = {
    val targetClass = classTag[T].runtimeClass
    srcs.par.map(src => mapper.map[T](src, targetClass).asInstanceOf[T]).seq
  }

  def tryMap[@miniboxed T: ClassTag](src: Any): Try[T] =
    Try { map[T](src) }
}
