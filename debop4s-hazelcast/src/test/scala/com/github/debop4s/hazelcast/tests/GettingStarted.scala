package com.github.debop4s.hazelcast.tests

import com.hazelcast.config.Config
import com.hazelcast.core.Hazelcast
import org.scalatest.{BeforeAndAfter, Matchers, FunSuite}

/**
 * GettingStarted
 * Created by debop on 2014. 2. 24.
 */
class GettingStarted extends FunSuite with Matchers with BeforeAndAfter {

    test("getting start") {
        val cfg = new Config()
        val instance = Hazelcast.newHazelcastInstance(cfg)

        val mapCustomers = instance.getMap[Int, String]("customers")
        mapCustomers.put(1, "Joe")
        mapCustomers.put(2, "Ali")
        mapCustomers.put(3, "Avi")

        println(s"Customer with key 1: ${mapCustomers.get(1)}")
        println(s"Map Size: ${mapCustomers.size()}")

        val queueCustomers = instance.getQueue[String]("customers")
        queueCustomers.offer("Tom")
        queueCustomers.offer("Mary")
        queueCustomers.offer("Jane")

        println(s"First customer: ${queueCustomers.poll()}")
        println(s"Second customer: ${queueCustomers.poll()}")
        println(s"Queue size: ${queueCustomers.size()}")

    }

}
