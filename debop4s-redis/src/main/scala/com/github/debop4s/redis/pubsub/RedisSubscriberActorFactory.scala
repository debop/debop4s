package com.github.debop4s.redis.pubsub

import akka.actor.Props
import redis.actors.RedisSubscriberActor
import scala.annotation.varargs

/**
 * Redis Pub/Sub 용 Subscriber 를 생성해주는 Factory 입니다.
 *
 * Created by debop on 2014. 2. 24.
 */
object RedisSubscriberActorFactory {

    implicit val akkaSystem = akka.actor.ActorSystem()

    /**
    * [[RedisSubscriberActor]] 를 상속받은 Subscribe용 Actor를 akka system에 등록합니다.
    *
    * @param actorClass Subscribe용 Actor의 수형
    * @param args Subscriber용 Actor 생성자의 인자들
    */
    @varargs
    def create[T <: RedisSubscriberActor](actorClass: Class[T], args: Any*) {
        val props = Props(actorClass, args: _*)
        akkaSystem.actorOf(props)
    }

    /**
    * [[RedisSubscriberActor]] 를 상속받은 Subscribe용 Actor를 akka system에 등록합니다.
    *
    * @param name 등록되는 actor의 이름
    * @param actorClass Subscribe용 Actor의 수형
    * @param args Subscriber용 Actor 생성자의 인자들
    */
    @varargs
    def create[T <: RedisSubscriberActor](name: String, actorClass: Class[T], args: Any*) {
        val props = Props(actorClass, args: _*)
        akkaSystem.actorOf(props, name)
    }
}
