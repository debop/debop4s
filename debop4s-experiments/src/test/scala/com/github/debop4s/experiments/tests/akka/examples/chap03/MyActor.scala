package com.github.debop4s.experiments.tests.akka.examples.chap03

import akka.actor.{Props, Actor}
import akka.event.Logging
import org.scalatest.FunSuite

/**
 * MyActor
 * Created by debop on 2014. 3. 3.
 */
class MyActor(val name: String = "actor") extends Actor {

    def this() {
        this("actor")
    }

    val log = Logging(context.system, this)

    override def receive: Actor.Receive = {
        case "test" => log.info("receive test")
        case _ => log.info("received unknown message")
    }
}

class Chap03Test extends FunSuite {

    implicit val akkaSystem = akka.actor.ActorSystem()

    test("props example") {
        val props1 = Props[MyActor]
        val props3 = Props(classOf[MyActor], "my actor")
    }

    test("props with args") {
        akkaSystem.actorOf(DemoActor.props("hello"))
    }
}

object DemoActor {

    def props(name: String): Props = Props(classOf[DemoActor], name)
}

class DemoActor(name: String) extends Actor {
    override def receive: Actor.Receive = {
        case x => // some behavior
    }
}
