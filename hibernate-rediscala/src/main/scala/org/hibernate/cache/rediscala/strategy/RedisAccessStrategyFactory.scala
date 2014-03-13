package org.hibernate.cache.rediscala.strategy

import org.hibernate.cache.rediscala.regions._
import org.hibernate.cache.spi.access.AccessType._
import org.hibernate.cache.spi.access._

/**
 * org.hibernate.cache.rediscala.strategy.AccessStrategyFactory
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 21. 오후 1:09
 */
trait RedisAccessStrategyFactory {

    def createEntityRegionAccessStrategy(entityRegion: RedisEntityRegion,
                                         accessType: AccessType): EntityRegionAccessStrategy

    def createCollectionRegionAccessStrategy(collectionRegion: RedisCollectionRegion,
                                             accessType: AccessType): CollectionRegionAccessStrategy

    def createNaturalIdRegionAccessStrategy(entityRegion: RedisNaturalIdRegion,
                                            accessType: AccessType): NaturalIdRegionAccessStrategy
}

object RedisAccessStrategyFactory {
    def apply(): RedisAccessStrategyFactory = new RedisAccessStrategyFactoryImpl()
}

class RedisAccessStrategyFactoryImpl extends RedisAccessStrategyFactory {


    override def createCollectionRegionAccessStrategy(collectionRegion: RedisCollectionRegion,
                                                      accessType: AccessType): CollectionRegionAccessStrategy = {
        accessType match {
            case READ_ONLY =>
                new ReadOnlyRedisCollectionRegionAccessStrategy(collectionRegion, collectionRegion.settings)
            case READ_WRITE =>
                new ReadWriteRedisCollectionRegionAccessStrategy(collectionRegion, collectionRegion.settings)
            case NONSTRICT_READ_WRITE =>
                new NonStrictReadWriteRedisCollectionRegionAccessStrategy(collectionRegion, collectionRegion.settings)
            case TRANSACTIONAL =>
                new TransactionalRedisCollectionAccessStrategy(collectionRegion, collectionRegion.settings)

            case _ => throw new IllegalArgumentException(s"unrecognized access strategy type. [$accessType]")
        }
    }

    override def createEntityRegionAccessStrategy(entityRegion: RedisEntityRegion,
                                                  accessType: AccessType): EntityRegionAccessStrategy = {

        accessType match {
            case READ_ONLY =>
                new ReadOnlyRedisEntityRegionAccessStrategy(entityRegion, entityRegion.settings)
            case READ_WRITE =>
                new ReadWriteRedisEntityRegionAccessStrategy(entityRegion, entityRegion.settings)
            case NONSTRICT_READ_WRITE =>
                new NonStrictReadWriteRedisEntityRegionAccessStrategy(entityRegion, entityRegion.settings)
            case TRANSACTIONAL =>
                new TransactionalRedisEntityRegionAccessStrategy(entityRegion, entityRegion.settings)

            case _ => throw new IllegalArgumentException(s"unrecognized access strategy type. [$accessType]")
        }
    }

    override def createNaturalIdRegionAccessStrategy(entityRegion: RedisNaturalIdRegion,
                                                     accessType: AccessType): NaturalIdRegionAccessStrategy = {
        accessType match {
            case READ_ONLY =>
                new ReadOnlyRedisNaturalIdRegionAccessStrategy(entityRegion, entityRegion.settings)
            case READ_WRITE =>
                new ReadWriteRedisNaturalIdRegionAccessStrategy(entityRegion, entityRegion.settings)
            case NONSTRICT_READ_WRITE =>
                new NonStrictReadWriteRedisNatualIdRegionAccessStrategy(entityRegion, entityRegion.settings)
            case TRANSACTIONAL =>
                new TransactionalRedisNatualIdRegionAccessStrategy(entityRegion, entityRegion.settings)

            case _ => throw new IllegalArgumentException(s"unrecognized access strategy type. [$accessType]")
        }
    }
}
