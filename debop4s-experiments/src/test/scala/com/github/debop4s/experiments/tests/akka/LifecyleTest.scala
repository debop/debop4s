package com.github.debop4s.experiments.tests.akka

import akka.actor.{Props, ActorSystem, Actor}
import com.github.debop4s.experiments.tests.AbstractExperimentTest

/**
 * LifecyleTest
 * @author Sunghyouk Bae
 */
class LifecyleTest extends AbstractExperimentTest {

    test("lifecycle test") {
        val system = ActorSystem("LifecycleDemo")
        val kenny = system.actorOf(Props[Kenny], name = "Kenny")

        println("sending kenny a simple String message")
        kenny ! "hello"
        Thread.sleep(1000)

        println("make kenny restart")
        kenny ! ForceRestart
        Thread.sleep(1000)

        println("stopping kenny")
        system.stop(kenny)

        println("shutting down system")
        system.shutdown()
    }
}

class Kenny extends Actor {
    println("entered the Kenny constructor")

    @throws(classOf[Exception])
    override def preStart() { println("kenny: preStart") }

    @throws(classOf[Exception])
    override def postStop() { println("kenny: postStop") }

    @throws(classOf[Exception])
    override def preRestart(reason: Throwable, message: Option[Any]) {
        println("kenny: preRestart")
        println(s"    MESSAGE: ${message.getOrElse("")}")
        println(s"    REASON: ${reason.getMessage}")
    }

    @throws(classOf[Exception])
    override def postRestart(reason: Throwable) {
        println("kenny: postRestart")
        println(s"    REASON: ${reason.getMessage}")
        super.postRestart(reason)
    }

    def receive = {
        case ForceRestart => throw new Exception("Boom!")
        case _ => println("Kenny received a message")
    }
}

case object ForceRestart

