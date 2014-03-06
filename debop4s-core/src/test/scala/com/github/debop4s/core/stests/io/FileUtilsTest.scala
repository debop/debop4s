package com.github.debop4s.core.stests.io

import com.github.debop4s.core.io.FileUtils
import com.github.debop4s.core.parallels.Promises
import com.github.debop4s.core.utils.{Charsets, Strings}
import java.nio.file.{StandardOpenOption, Paths, Path}
import org.junit.Test
import org.scalatest.junit.AssertionsForJUnit
import org.slf4j.LoggerFactory
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.duration._

/**
 * com.github.debop4s.core.tests.io.FileUtilsTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 16. 오전 12:10
 */
class FileUtilsTest extends AssertionsForJUnit {

    lazy val log = LoggerFactory.getLogger(getClass)

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
        val lines = new ArrayBuffer[String](lineCount)
        (0 until lineCount).foreach(x => lines += TEST_TEXT.trim)

        try {
            FileUtils.createFile(path)
            FileUtils.write(path, lines, Charsets.UTF_8, StandardOpenOption.WRITE)

            val readLines = FileUtils.readAllLines(path)
            log.debug(s"readLines=${ readLines.size }, lines=${ lines.size }")
            assert(readLines.size == lines.size)
        }
        finally {
            FileUtils.deleteIfExists(path)
        }
    }

    @Test
    def asyncReadAndWrite() {
        val lineCount = 1000
        val path = Paths.get("asyncFileUtils.txt")
        FileUtils.deleteIfExists(path)

        try {
            val writeResult = FileUtils.writeAsync(path,
                                                      Strings.replicate(TEST_TEXT, lineCount).getBytes(FileUtils.UTF8),
                                                      StandardOpenOption.CREATE,
                                                      StandardOpenOption.WRITE)
            Promises.await(writeResult)
            val readResult = FileUtils.readAllLinesAsync(path, FileUtils.UTF8, StandardOpenOption.READ)
            val lines = Promises.await(readResult, 60 seconds)
            assert(lines.size == lineCount)
        } finally {
            FileUtils.deleteIfExists(path)
        }
    }
}
