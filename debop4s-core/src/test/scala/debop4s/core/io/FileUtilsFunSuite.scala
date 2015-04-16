package debop4s.core.io

import java.nio.file.{Path, Paths, StandardOpenOption}
import java.util

import debop4s.core._
import debop4s.core.concurrent._
import debop4s.core.utils.{Charsets, Strings}
import org.junit.Test

import scala.concurrent.duration._
import scala.util.{Failure, Success}

class FileUtilsFunSuite extends AbstractCoreFunSuite {

  val TEST_TEXT: String = "동해물과 백두산이 마르고 닳도록, 하느님이 보우하사 우리나라 만세!!! Hello World. 안녕 세계여\n"

  @Test
  def createAndDelete() {
    val path = Paths.get("./테스트.txt")
    FileUtils.deleteIfExists(path)
    try {
      FileUtils.createFile(path)
      assert(FileUtils.exists(path))

      FileUtils.delete(path)
      assert(!FileUtils.exists(path))
    }
    finally {
      FileUtils.deleteIfExists(path)
    }
  }

  @Test
  def binaryReadAndWrite() {
    val path: Path = Paths.get("test.dat")
    FileUtils.deleteIfExists(path)

    val bytes: Array[Byte] = Strings.getUtf8Bytes(TEST_TEXT)
    try {
      FileUtils.createFile(path)
      FileUtils.write(path, bytes, StandardOpenOption.WRITE)
      val readBytes = FileUtils.readAllBytes(path)
      assert(readBytes === bytes)
    }
    finally {
      FileUtils.deleteIfExists(path)
    }
  }

  @Test
  def charReadAndWrite() {
    val path = Paths.get("test.txt")
    FileUtils.deleteIfExists(path)

    val lineCount = 100
    val lines = new util.ArrayList[String](lineCount)

    (0 until lineCount).foreach { _ => lines.add(TEST_TEXT.trim) }

    try {
      FileUtils.createFile(path)
      FileUtils.write(path, lines, Charsets.UTF_8, StandardOpenOption.WRITE)

      val readLines = FileUtils.readAllLines(path)
      trace(s"readLines=${ readLines.size }, lines=${ lines.size }")

      readLines.size shouldEqual lines.size
    }
    finally {
      FileUtils.deleteIfExists(path)
    }
  }

  @Test
  def asyncReadAndWrite() {
    val lineCount = 100
    val path = Paths.get("asyncFileUtils.txt")
    FileUtils.deleteIfExists(path)

    try {
      val writeResult =
        FileUtils.writeAsync(path,
          Strings.replicate(TEST_TEXT, lineCount).getBytes(FileUtils.UTF8),
          StandardOpenOption.CREATE,
          StandardOpenOption.WRITE)

      writeResult.await

      val readResult = FileUtils.readAllLinesAsync(path, FileUtils.UTF8, StandardOpenOption.READ)
      readResult.await(60 seconds) match {
        case Success(lines) =>
          lines.size shouldEqual lineCount
        case Failure(e) =>
          fail(e)
      }
    } finally {
      FileUtils.deleteIfExists(path)
    }
  }
}

