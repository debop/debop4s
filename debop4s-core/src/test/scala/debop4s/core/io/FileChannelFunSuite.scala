package debop4s.core.io

import java.io.{BufferedReader, ByteArrayInputStream}
import java.nio.ByteBuffer
import java.nio.channels.AsynchronousFileChannel
import java.nio.charset.Charset
import java.nio.file.{Files, Path, Paths, StandardOpenOption}

import debop4s.core.AbstractCoreFunSuite
import debop4s.core.utils.{Charsets, Strings}

import scala.collection.JavaConversions._
import scala.collection.mutable.ArrayBuffer

/**
 * FileChannelFunSuite
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 15. 오후 11:44
 */
class FileChannelFunSuite extends AbstractCoreFunSuite {

  val TEST_TEXT = "동해물과 백두산이 마르고 닳도록, 하느님이 보우하사 우리나라 만세!!! Hello World. 안녕 세계여 \n"

  test("buffered stream") {
    val path = Paths.get("channel.txt")
    val writer = Files.newBufferedWriter(path, Charsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND)
    try {
      (0 until 100).foreach {
        x => writer.write(TEST_TEXT)
      }
      writer.flush()
    } finally {
      writer.close()
    }
    val lines = Files.readAllLines(path, Charsets.UTF_8)
    lines.foreach {
      line => log.trace(s"line:$line")
    }
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
        log.trace("Do something else while writing...")
        Thread.sleep(1)
      }
      log.trace("Write done: " + result.isDone)
      log.trace("Bytes write: " + result.get)
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
        log.trace("Do something else while reading...")
        Thread.sleep(1)
      }
      log.trace("Read done: " + result.isDone)
      log.trace("Bytes read: " + result.get)

      buffer.flip
      val bytes: Array[Byte] = buffer.array
      val lines = readAllLines(bytes, Charsets.UTF_8)
      lines.foreach(line => log.trace(s"line:$line"))
      buffer.clear
    } finally {
      assert(fc != null)
      fc.close()
    }
  }

  def readAllLines(bytes: Array[Byte], charset: Charset): Seq[String] = {
    if (bytes == null || bytes.length == 0)
      ArrayBuffer[String]()

    val is = new ByteArrayInputStream(bytes)
    val in = new java.io.InputStreamReader(is, charset)
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
