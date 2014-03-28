package com.github.debop4s.experiments.tests.akka

import akka.actor.{Props, ActorSystem, Actor}
import com.github.debop4s.experiments.tests.AbstractExperimentTest

/**
 * 상태에 따라 특정 메시지만 받도록 합니다.
 * @author Sunghyouk Bae
 */
class BecomeHulkExample extends AbstractExperimentTest {

    test("change actor state") {
        val system = ActorSystem("beHulk")
        val actor = system.actorOf(Props[DavidBanner], name = "DavidBanner")

        actor ! ActNormalMessage
        actor ! TryToFindSolution
        actor ! BadGuysMakeMeAngry

        Thread.sleep(100)

        actor ! ActNormalMessage
        system.shutdown()
    }
}

case object ActNormalMessage

case object TryToFindSolution

case object BadGuysMakeMeAngry

class DavidBanner extends Actor {

    import context._

    // 처음 받는 메시지에 따라 receive 함수를 선택한다.
    override def receive: Receive = {
        case BadGuysMakeMeAngry => become(angryState)
        case ActNormalMessage => become(normalState)
    }

    // 헐크가 화가난 상태에서는 정상으로 돌아가는 것만 받고, 나머지는 무시한다.
    def angryState: Receive = {
        case ActNormalMessage =>
            println("휴, 다시 데이비드로 돌아간다")
            become(normalState)
    }

    // 정상인 상태에서는 정상적인 메시지는 무시하고, 나머지를 처리한다.
    def normalState: Receive = {
        case TryToFindSolution => println("Looking for solution to my problem...")

        case BadGuysMakeMeAngry =>
            println("화가 난다... 헐크 되겠다...")
            become(angryState)
    }
}
