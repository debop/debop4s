package org.hibernate.cache.rediscala.tests.serializer

import org.hibernate.cache.rediscala.serializer.MessagePackRedisSerializer
import org.hibernate.cache.rediscala.tests.AbstractHibernateRedisTest
import org.msgpack.ScalaMessagePack._
import org.msgpack._


/**
 * MessagePackSerializerTest
 * @author Sunghyouk Bae
 */
class MessagePackRedisSerializerTest extends AbstractHibernateRedisTest {

    val serializer = new MessagePackRedisSerializer()

    test("Message Pack Value 사용하기") {

        messagePack.register(classOf[MessagePackSampleClass])

        val list = List("sampleClassName", 5)
        val b = ScalaMessagePack.writeV(list)
        val value = ScalaMessagePack.readAsValue(b)

        val name: String = value(0)
        val number: Int = value(1)
        assert(name.equals(list(0).asInstanceOf[String]))
        assert(number == list(1).asInstanceOf[Int])

        val deser = read[MessagePackSampleClass](b)
        assert(deser != null)
        assert(deser.name == name)
    }

    test("MessagePack") {
        messagePack.register(classOf[MessagePackSampleClass])

        val v = new MessagePackSampleClass("aaaa", 123)
        val b = write(v)
        val d = read[MessagePackSampleClass](b)

        assert(d.name == v.name)
        assert(d.number == v.number)
    }

    test("MessagePackSerializer") {
        messagePack.register(classOf[MessagePackSampleClass])

        val v = new MessagePackSampleClass("aaaa", 123)

        val b = serializer.serialize(v)
        val d = serializer.deserialize[MessagePackSampleClass](b)

        assert(d.name == v.name)
        assert(d.number == v.number)

        val d2 = serializer.deserialize(b, classOf[MessagePackSampleClass])
        assert(d2.name == v.name)
        assert(d2.number == v.number)

        val className = classOf[MessagePackSampleClass].getName
        val d3 = serializer.deserialize(b, Class.forName(className)).asInstanceOf[MessagePackSampleClass]
        assert(d3.name == v.name)
        assert(d3.number == v.number)

    }
}


class MessagePackSampleClass(var name: String = "", var number: Int) {
    def this() {
        this("", 0)
    }
}
