package com.github.debop4s.experiments.tests.akka

import akka.actor.{PoisonPill, Props, ActorSystem, Actor}
import com.github.debop4s.experiments.tests.AbstractExperimentTest
import scala.concurrent.Await
import scala.concurrent.duration._

/**
 * SystemStopExample
 * @author Sunghyouk Bae
 */
class SystemStopExample extends AbstractExperimentTest {

    test("stopping actor") {
        val system = ActorSystem("systemStopExample")
        val actor = system.actorOf(Props[TestActor], name = "test")

        actor ! "hello"

        // stop our actor
        system.stop(actor)
        system.shutdown()
    }

    test("sending PoisonPill") {
        val system = ActorSystem("systemStopExample")
        val actor = system.actorOf(Props[TestActor], name = "test")

        actor ! "before PoisonPill"

        actor ! PoisonPill

        actor ! "after PoisonPill"
        actor ! "hello?!"

        system.shutdown()
    }

    test("graceful stop") {
        implicit val system = ActorSystem("systemStopExample")
        val actor = system.actorOf(Props[TestActor], name = "test")

        try {
            val stopped = akka.pattern.gracefulStop(actor, 2 seconds)
            Await.ready(stopped, 3 seconds)
            println("testActor was stopped")
        } catch {
            case e: Exception => e.printStackTrace()
        } finally {
            system.shutdown()
        }
    }
}

class TestActor extends Actor {
    def receive = {
        case s: String => println(s"Message received: $s")
        case _ => println("a message was received")
    }

    override def postStop() {
        println("Test Actor::postStop called!")
    }
}
