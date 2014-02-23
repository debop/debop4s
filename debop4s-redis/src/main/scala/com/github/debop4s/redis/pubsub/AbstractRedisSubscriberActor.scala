package com.github.debop4s.redis.pubsub

import java.net.InetSocketAddress
import redis.actors.RedisSubscriberActor
import redis.api.pubsub.{Message, PMessage}

/**
 * AbstractRedisSubscriberActor
 * Created by debop on 2014. 2. 24.
 */
abstract class AbstractRedisSubscriberActor(override val address: InetSocketAddress,
                                            val channels: Seq[String],
                                            val patterns: Seq[String] = Nil)
    extends RedisSubscriberActor(address, channels, patterns) {

    /**
     * Redis Pub/Sub channels 에 데이터가 들어오면 호출되는 메소드입니다.
     */
    override def onMessage(m: Message) {}

    /**
     * Redis Pub/Sub channels 에 Patten에 해당하는 데이터가 들어오면 호출되는 메소드입니다.
     */
    override def onPMessage(pm: PMessage) {}

    //    override def subscribe(channels: String*) = {
    //        super.subscribe(channels: _*)
    //        log.debug(s"Redis Pub/Sub 채널 가입합니다. channels=$channels")
    //    }
    //
    //    override def unsubscribe(channels: String*) {
    //        super.unsubscribe(channels: _*)
    //        log.debug(s"Redis Pub/Sub 채널 가입을 해지합니다. channels=$channels")
    //    }
    //
    //    override def psubscribe(patterns: String*) {
    //        super.psubscribe(patterns: _*)
    //        log.debug(s"Redis Pub/Sub 패턴 가입합니다. patterns=$patterns")
    //    }
    //
    //    override def punsubscribe(patterns: String*) {
    //        super.punsubscribe(patterns: _*)
    //        log.debug(s"Redis Pub/Sub 패턴 가입을 해지합니다. patterns=$patterns")
    //    }
}
