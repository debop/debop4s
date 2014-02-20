package com.github.debop4s.core.utils

import org.modelmapper.ModelMapper
import org.modelmapper.config.Configuration.AccessLevel
import org.modelmapper.convention.MatchingStrategies
import org.slf4j.LoggerFactory
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.reflect._

/**
 * [[ModelMapper]]
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 12. 오후 2:14
 */
object Mappers {

    lazy val log = LoggerFactory.getLogger(getClass)

    val mapper = new ModelMapper()

    mapper.getConfiguration
    .setFieldMatchingEnabled(true)
    .setMatchingStrategy(MatchingStrategies.STANDARD)
    .setFieldAccessLevel(AccessLevel.PRIVATE)

    def map(src: AnyRef, dest: AnyRef) = mapper.map(src, dest)

    def map[D: ClassTag](src: AnyRef) = mapper.map[D](src, classTag[D].runtimeClass)

    def mapArray[D: ClassTag](src: AnyRef*): Seq[D] =
        mapList(src.toIterable).toIndexedSeq

    def mapList[D: ClassTag](srcs: Iterable[_]): Iterable[D] = {
        for (src <- srcs) yield {
            mapper.map[D](src, classTag[D].runtimeClass)
        }
    }

    def mapAsync[D: ClassTag](src: AnyRef): Future[D] = future {
        map[D](src)
    }

    def mapArrayAsync[D: ClassTag](srcs: AnyRef*): Future[Array[D]] = future {
        mapList(srcs.toIterable).toArray
    }

    def mapListAsync[D: ClassTag](srcs: Iterable[_]): Future[List[D]] = future {
        mapList[D](srcs).toList
    }
}