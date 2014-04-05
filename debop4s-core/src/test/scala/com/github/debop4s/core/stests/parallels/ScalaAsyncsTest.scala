package com.github.debop4s.core.stests.parallels

import com.github.debop4s.core._
import com.github.debop4s.core.stests.AbstractCoreTest
import concurrent.ExecutionContext.Implicits.global
import concurrent._
import scala.async.Async._

/**
 * ScalaAsyncsTest
 * Created by debop on 2014. 4. 4.
 */
class ScalaAsyncsTest extends AbstractCoreTest {

    test("scala-async async/await example") {
        val future1 = future {42}
        val future2 = future {84}

        async {
            println("computing...")
            val answer = await(future1)
            println(s"found the answer: $answer")
        }

        val sum = async {
            await(future1) + await(future2)
        }
        assert(sum.result() == (42 + 84))
    }

}
