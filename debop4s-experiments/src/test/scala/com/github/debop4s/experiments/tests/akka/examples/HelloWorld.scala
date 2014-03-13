package com.github.debop4s.experiments.tests.akka.examples

import akka.actor.{Props, Actor}
import org.scalatest.FunSuite

/**
 * HelloWorld
 * Created by debop on 2014. 3. 2.
 */
class HelloWorld extends Actor {

    override def preStart() {
        // create Greeter actor
        val greeter = context.actorOf(Props[Greeter], "greeter")
        // tell it to perform the greeting
        greeter ! Greeter.Greet
    }

    override def receive: Actor.Receive = {
        // when the greeter is done, stop this actor and with it the application
        case Greeter.Done => context.stop(self)
    }
}

object Greeter {

    case object Greet

    case object Done

}

class Greeter extends Actor {
    override def receive: Actor.Receive = {
        case Greeter.Greet =>
            println("Hello World")
            sender ! Greeter.Done
    }
}


class HelloWorldTest extends FunSuite {

    implicit val akkaSystem = akka.actor.ActorSystem()

    test("hello world actor") {

        val helloActor = akkaSystem.actorOf(Props[HelloWorld], "helloworld")
    }
}
