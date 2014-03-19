package com.github.debop4s.core.stests.io

import com.github.debop4s.core.utils.{Strings, Charsets}
import java.io.{BufferedReader, InputStreamReader, ByteArrayInputStream}
import java.nio.ByteBuffer
import java.nio.channels.AsynchronousFileChannel
import java.nio.charset.Charset
import java.nio.file.{Path, StandardOpenOption, Files, Paths}
import org.scalatest.{BeforeAndAfter, Matchers, FunSuite}
import org.slf4j.LoggerFactory
import scala.collection.JavaConversions._
import scala.collection.mutable.ArrayBuffer

/**
 * com.github.debop4s.core.tests.io.FileChannelTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 15. 오후 11:44
 */
class FileChannelTest extends FunSuite with Matchers with BeforeAndAfter {

    lazy val log = LoggerFactory.getLogger(getClass)

    val TEST_TEXT = "동해물과 백두산이 마르고 닳도록, 하느님이 보우하사 우리나라 만세!!! Hello World. 안녕 세계여 \n"

    test("buffered stream") {
        val path = Paths.get("channel.txt")
        val writer = Files.newBufferedWriter(path, Charsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND)
        try {
            (0 until 100).foreach(_ => writer.write(TEST_TEXT))
            writer.flush()
        } finally {
            writer.close()
        }
        val lines = Files.readAllLines(path, Charsets.UTF_8)
        lines.foreach {line => log.debug(s"line:$line")}
        Files.deleteIfExists(path)
    }

    test("asynchronous file channel") {
        val path: Path = Paths.get("async.txt")
        var fc: AsynchronousFileChannel = null
        try {
            fc = AsynchronousFileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE)
            val buffer: ByteBuffer = ByteBuffer.wrap(Strings.replicate(TEST_TEXT, 100).getBytes(Charsets.UTF_8))
            val result = fc.write(buffer, 0)
            while (!result.isDone) {
                System.out.println("Do something else while writing...")
                Thread.sleep(1)
            }
            System.out.println("Write done: " + result.isDone)
            System.out.println("Bytes write: " + result.get)
        }
        finally {
            assert(fc != null)
            fc.close()
        }

        fc = null
        try {
            fc = AsynchronousFileChannel.open(path, StandardOpenOption.READ, StandardOpenOption.DELETE_ON_CLOSE)
            val buffer: ByteBuffer = ByteBuffer.allocate(fc.size.asInstanceOf[Int])
            val result = fc.read(buffer, 0)
            while (!result.isDone) {
                System.out.println("Do something else while reading...")
                Thread.sleep(1)
            }
            System.out.println("Read done: " + result.isDone)
            System.out.println("Bytes read: " + result.get)
            buffer.flip
            val bytes: Array[Byte] = buffer.array
            val lines = readAllLines(bytes, Charsets.UTF_8)
            lines.foreach(line => log.debug(s"line:$line"))
            buffer.clear
        } finally {
            assert(fc != null)
            fc.close()
        }
    }

    def readAllLines(bytes: Array[Byte], charset: Charset): IndexedSeq[String] = {
        if (bytes == null || bytes.length == 0)
            ArrayBuffer()

        val is = new ByteArrayInputStream(bytes)
        val in = new InputStreamReader(is, charset)
        val br = new BufferedReader(in)

        val lines = ArrayBuffer[String]()
        while (true) {
            val line = br.readLine
            if (line == null)
                return lines
            lines += line
        }
        lines
    }
}
