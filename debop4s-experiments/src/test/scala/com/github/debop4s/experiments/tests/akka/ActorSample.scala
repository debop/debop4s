package com.github.debop4s.experiments.tests.akka

import akka.actor.{Props, ActorSystem, Actor}
import com.github.debop4s.experiments.tests.AbstractExperimentTest

/**
 * ActorSample
 * @author Sunghyouk Bae
 */
class ActorSample extends AbstractExperimentTest {

    test("HelloActor") {
        val system = ActorSystem("AkkaSystem")
        val helloActor = system.actorOf(Props[HelloActor], name = "helloActor")

        helloActor ! "hello"
        helloActor ! "buenos dias"

        // shut down akka system
        system.shutdown()
    }
}

class HelloActor extends Actor {
    override def receive = {
        case "hello" => println("안녕")
        case _ => println("뭐라고?")
    }
}
