package kr.debop4s.core.io

import java.io._
import java.nio.ByteBuffer
import java.nio.channels.AsynchronousFileChannel
import java.nio.charset.Charset
import java.nio.file._
import java.nio.file.attribute.{BasicFileAttributes, FileAttribute}
import kr.debop4s.core.logging.Logger
import scala.collection.JavaConversions._
import scala.collection.mutable.ArrayBuffer
import scala.concurrent
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration._

/**
 * File 관련 Object
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 10. 오후 10:48
 */
object FileUtils {

    val log = Logger(this.getClass)

    val DEFUALT_BUFFER_SIZE = 4096
    val UTF8 = Charset.forName("UTF-8")

    def combine(base: String, others: String*): Path = Paths.get(base, others: _*)

    def combine(base: Path, others: String*): Path = {
        var result = base
        others.foreach(x => result = result.resolve(x))
        result
    }

    def combine(base: Path, other: Path) = base.resolve(other)

    def createDirectory(dir: Path, attrs: FileAttribute[_]*): Path =
        Files.createDirectory(dir, attrs: _*)

    def createDirectories(dir: Path, attrs: FileAttribute[_]*): Path =
        Files.createDirectories(dir, attrs: _*)

    def createFile(path: Path, attrs: FileAttribute[_]*): Path = {
        log.debug(s"create file. paht=[$path], attrs=[$attrs]")
        Files.createFile(path, attrs: _*)
    }

    def copy(src: Path, target: Path, options: CopyOption*) {
        Files.copy(src, target, options: _*)
    }

    def copyAsync(src: Path, target: Path, options: CopyOption*): concurrent.Future[Unit] = future {
                                                                                                       copy(src, target, options: _*)
                                                                                                   }

    def move(src: Path, dest: Path, options: CopyOption*) {
        Files.move(src, dest, options: _*)
    }

    def moveAsync(src: Path, dest: Path, options: CopyOption*): concurrent.Future[Unit] = future {
                                                                                                     move(src, dest, options: _*)
                                                                                                 }

    def delete(path: Path) {
        log.debug(s"delete file. path=$path")
        Files.delete(path)
    }

    def deleteIfExists(path: Path) {
        if (exists(path))
            delete(path)
    }

    def deleteDirectory(dir: Path, deep: Boolean = true) {
        if (!deep)
            deleteIfExists(dir)
        else {
            val visitor = new SimpleFileVisitor[Path] {
                override def postVisitDirectory(dir: Path, exc: IOException) = {
                    Files.delete(dir)
                    FileVisitResult.CONTINUE
                }

                override def visitFile(file: Path, attrs: BasicFileAttributes) = {
                    Files.delete(file)
                    FileVisitResult.CONTINUE
                }
            }
            Files.walkFileTree(dir, visitor)
        }
    }

    def deleteDirectoryAsync(dir: Path, deep: Boolean = true): concurrent.Future[Unit] = future {
                                                                                                    deleteDirectory(dir, deep)
                                                                                                }


    def exists(path: Path): Boolean = Files.exists(path, LinkOption.NOFOLLOW_LINKS)

    def exists(path: Path, linkOptions: LinkOption*): Boolean =
        Files.exists(path, linkOptions: _*)

    def readAllBytes(path: Path): Array[Byte] = Files.readAllBytes(path)

    def readAllBytesAsync(path: Path): concurrent.Future[Array[Byte]] =
        readAllBytesAsync(path, StandardOpenOption.READ)

    def readAllBytesAsync(path: Path, openOptions: OpenOption*): concurrent.Future[Array[Byte]] = future {
                                                                                                             assert(path != null)

                                                                                                             val fileChannel = AsynchronousFileChannel
                                                                                                                 .open(path, openOptions: _*)
                                                                                                             try {
                                                                                                                 val buffer = ByteBuffer
                                                                                                                     .allocate(fileChannel
                                                                                                                                   .size()
                                                                                                                                   .asInstanceOf[Int])
                                                                                                                 val result = fileChannel
                                                                                                                     .read(buffer, 0)
                                                                                                                 result.get()
                                                                                                                 buffer.flip()
                                                                                                                 buffer.array()
                                                                                                             } finally {
                                                                                                                 fileChannel.close()
                                                                                                             }
                                                                                                         }

    def readAllLines(path: Path, cs: Charset = UTF8): List[String] = {
        log.debug(s"read all lines. path=$path, charset=$cs")
        Files.readAllLines(path, cs).toList
    }

    def readAllLines(is: InputStream): IndexedSeq[String] = {
        readAllLines(is, UTF8)
    }

    def readAllLines(is: InputStream, cs: Charset): IndexedSeq[String] = {
        val lines = ArrayBuffer[String]()
        val reader = new BufferedReader(new InputStreamReader(is, cs))
        try {
            var line = reader.readLine()
            while (line != null) {
                lines += line
                line = reader.readLine()
            }
        } finally {
            reader.close()
        }
        lines
    }

    def readAllLines(input: Array[Byte]): IndexedSeq[String] = {
        readAllLines(input, UTF8)
    }

    def readAllLines(input: Array[Byte], cs: Charset): IndexedSeq[String] = {
        val is = new ByteArrayInputStream(input)
        try {
            readAllLines(is, cs)
        } finally {
            is.close()
        }
    }

    def readAllLinesAsync(path: Path, cs: Charset, openOptions: OpenOption*) = future {
                                                                                          val future = readAllBytesAsync(path, openOptions: _*)
                                                                                          readAllLines(Await
                                                                                                           .result(future, 60 seconds), cs)
                                                                                      }

    def readAllLinesAsync(is: InputStream): Future[IndexedSeq[String]] = {
        readAllLinesAsync(is, UTF8)
    }

    def readAllLinesAsync(is: InputStream, cs: Charset): Future[IndexedSeq[String]] = future {
                                                                                                 readAllLines(is, cs)
                                                                                             }

    def readAllLinesAsync(input: Array[Byte]): Future[IndexedSeq[String]] = {
        readAllLinesAsync(input, UTF8)
    }

    def readAllLinesAsync(input: Array[Byte], cs: Charset): Future[IndexedSeq[String]] = future {
                                                                                                    readAllLines(input, cs)
                                                                                                }

    def write(path: Path, input: Array[Byte]): Path = {
        write(path, input, StandardOpenOption.CREATE, StandardOpenOption.WRITE)
    }

    def write(path: Path, input: Array[Byte], options: OpenOption*): Path = {
        Files.write(path, input, options: _*)
    }

    def write(path: Path, lines: Iterable[String], cs: Charset = UTF8): Path = {
        write(path, lines, cs, StandardOpenOption.CREATE, StandardOpenOption.WRITE)
    }

    def write(path: Path, lines: Iterable[String], cs: Charset, options: OpenOption*): Path = {
        Files.write(path, lines.toSeq, cs, options: _*)
        // write(path, lines, cs, options: _*)
    }

    def writeAsync(path: Path, input: Array[Byte]): Future[Int] = {
        writeAsync(path, input, StandardOpenOption.CREATE, StandardOpenOption.WRITE)
    }

    def writeAsync(path: Path, input: Array[Byte], options: OpenOption*): Future[Int] = {
        val promise = Promise[Int]()

        val fileChannel = AsynchronousFileChannel.open(path, options: _*)
        try {
            val future = fileChannel.write(ByteBuffer.wrap(input), 0)
            promise.success(future.get())
        } finally {
            fileChannel.close()
        }
        promise.future
    }

    def writeAsync(path: Path, lines: Iterable[String], cs: Charset = UTF8): Future[Int] = {
        writeAsync(path, lines, cs, StandardOpenOption.CREATE, StandardOpenOption.WRITE)
    }

    def writeAsync(path: Path, lines: Iterable[String], cs: Charset, options: OpenOption*): Future[Int] = {
        val allText = lines.mkString(System.lineSeparator())
        writeAsync(path, cs.encode(allText).array(), options: _*)
    }
}
