package debop4s.rediscala.pubsub

import akka.actor.{ActorRef, Props}
import debop4s.rediscala.utils.AkkaUtil._
import redis.actors.RedisSubscriberActor

import scala.annotation.varargs

/**
 * Redis Pub/Sub 용 Subscriber 를 생성해주는 Factory 입니다.
 *
 * Created by debop on 2014. 2. 24.
 */
object RedisSubscriberActorFactory {

  /**
   * [[RedisSubscriberActor]] 를 상속받은 Subscribe용 Actor를 akka system에 등록합니다.
   *
   * @param actorClass Subscribe용 Actor의 수형
   * @param args Subscriber용 Actor 생성자의 인자들
   */
  @varargs
  def create[T <: RedisSubscriberActor](actorClass: Class[T], args: Any*): ActorRef = {
    val props = Props(actorClass, args: _*)
    actorSystem.actorOf(props)
  }

  /**
   * [[RedisSubscriberActor]] 를 상속받은 Subscribe용 Actor를 akka system에 등록합니다.
   *
   * @param name 등록되는 actor의 이름
   * @param actorClass Subscribe용 Actor의 수형
   * @param args Subscriber용 Actor 생성자의 인자들
   */
  @varargs
  def create[T <: RedisSubscriberActor](name: String, actorClass: Class[T], args: Any*): ActorRef = {
    val props = Props(actorClass, args: _*)
    actorSystem.actorOf(props, name)
  }
}
