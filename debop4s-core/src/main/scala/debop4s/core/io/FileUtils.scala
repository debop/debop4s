package debop4s.core.io

import java.io._
import java.nio.ByteBuffer
import java.nio.channels.AsynchronousFileChannel
import java.nio.charset.Charset
import java.nio.file._
import java.nio.file.attribute.{BasicFileAttributes, FileAttribute}
import java.util.concurrent.TimeUnit
import org.slf4j.LoggerFactory
import scala.annotation.varargs
import scala.collection.JavaConversions._
import scala.collection.mutable.ArrayBuffer
import scala.concurrent
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent._
import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}

/**
 * File 관련 Object
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 10. 오후 10:48
 */
object FileUtils {

  private lazy val log = LoggerFactory.getLogger(getClass)

  val DEFUALT_BUFFER_SIZE = 4096
  val UTF8 = Charset.forName("UTF-8")

  @varargs
  def combine(base: String, others: String*): Path = Paths.get(base, others: _*)

  @varargs
  def combine(base: Path, others: String*): Path = {
    var result = base
    others.foreach {
      x => result = result.resolve(x)
    }
    result
  }

  def combine(base: Path, other: Path) = base.resolve(other)

  @varargs
  def createDirectory(dir: Path, attrs: FileAttribute[_]*): Path =
    Files.createDirectory(dir, attrs: _*)

  @varargs
  def createDirectories(dir: Path, attrs: FileAttribute[_]*): Path =
    Files.createDirectories(dir, attrs: _*)

  @varargs
  def createFile(path: Path, attrs: FileAttribute[_]*): Path = {
    log.debug(s"create file. paht=[$path], attrs=[$attrs]")
    Files.createFile(path, attrs: _*)
  }

  @varargs
  def copy(src: Path, target: Path, options: CopyOption*) {
    Files.copy(src, target, options: _*)
  }

  @varargs
  def copyAsync(src: Path, target: Path, options: CopyOption*) = future {
    copy(src, target, options: _*)
  }

  @varargs
  def move(src: Path, dest: Path, options: CopyOption*) {
    Files.move(src, dest, options: _*)
  }

  @varargs
  def moveAsync(src: Path, dest: Path, options: CopyOption*) = future {
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

  @inline
  def deleteDirectory(dir: Path, deep: Boolean = true) {
    if (!deep) {
      deleteIfExists(dir)
    } else {
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

  def deleteDirectoryAsync(dir: Path, deep: Boolean = true) = future {
    deleteDirectory(dir, deep)
  }

  def exists(path: Path): Boolean =
    Files.exists(path, LinkOption.NOFOLLOW_LINKS)

  @varargs
  def exists(path: Path, linkOptions: LinkOption*): Boolean =
    Files.exists(path, linkOptions: _*)

  def readAllBytes(path: Path): Array[Byte] = Files.readAllBytes(path)

  def readAllBytesAsync(path: Path): concurrent.Future[Array[Byte]] =
    readAllBytesAsync(path, StandardOpenOption.READ)

  @varargs
  @inline
  def readAllBytesAsync(path: Path, openOptions: OpenOption*): Future[Array[Byte]] = future {
    assert(path != null)

    var fileChannel = None: Option[AsynchronousFileChannel]

    try {
      fileChannel = Some(AsynchronousFileChannel.open(path, openOptions: _*))
      val buffer = ByteBuffer.allocate(fileChannel.get.size().toInt)
      val result = fileChannel.get.read(buffer, 0)
      result.get()
      buffer.flip()
      buffer.array()
    } finally {
      if (fileChannel.isDefined)
        fileChannel.get.close()
    }
  }

  def readAllLines(path: Path, cs: Charset = UTF8): List[String] = {
    log.debug(s"read all lines. path=$path, charset=$cs")
    Files.readAllLines(path, cs).toList
  }

  def readAllLines(is: InputStream): Try[Seq[String]] = {
    readAllLines(is, UTF8)
  }

  @inline
  def readAllLines(is: InputStream, cs: Charset): Try[Seq[String]] = Try {
    val lines = ArrayBuffer[String]()
    val br = Try(new BufferedReader(new InputStreamReader(is, cs)))

    br match {
      case Success(reader) =>
        var line = reader.readLine()
        while (line != null) {
          lines += line
          line = reader.readLine()
        }
      case Failure(e) =>
        throw new RuntimeException("Cannot read file.", e)
    }
    lines
  }

  def readAllLines(input: Array[Byte]): Try[Seq[String]] = {
    readAllLines(input, UTF8)
  }

  def readAllLines(input: Array[Byte], cs: Charset): Try[Seq[String]] = {
    // scala 고유의 Option, Try 기능을 활용합니다.
    Try(new ByteArrayInputStream(input)) match {
      case Success(is) =>
        val results = readAllLines(is, cs)
        is.close()
        results
      case Failure(e) =>
        log.error("Fail to read bytes.", e)
        throw new RuntimeException("Fail to read bytes.", e)
    }
    //        val is = new ByteArrayInputStream(input)
    //        try {
    //            readAllLines(is, cs)
    //        } finally {
    //            is.close()
    //        }
  }

  @varargs
  def readAllLinesAsync(path: Path, cs: Charset, openOptions: OpenOption*): Future[Try[Seq[String]]] = future {
    val future = readAllBytesAsync(path, openOptions: _*)
    readAllLines(Await.result(future, 60 seconds), cs)
  }

  def readAllLinesAsync(is: InputStream): Future[Try[Seq[String]]] = {
    readAllLinesAsync(is, UTF8)
  }

  def readAllLinesAsync(is: InputStream, cs: Charset): Future[Try[Seq[String]]] = future {
    readAllLines(is, cs)
  }

  def readAllLinesAsync(input: Array[Byte]): Future[Try[Seq[String]]] = {
    readAllLinesAsync(input, UTF8)
  }

  def readAllLinesAsync(input: Array[Byte], cs: Charset): Future[Try[Seq[String]]] = future {
    readAllLines(input, cs)
  }

  def write(path: Path, input: Array[Byte]): Try[Path] = {
    write(path, input, StandardOpenOption.CREATE, StandardOpenOption.WRITE)
  }

  @varargs
  def write(path: Path, input: Array[Byte], options: OpenOption*): Try[Path] = Try {
    Files.write(path, input, options: _*)
  }

  def write(path: Path, lines: Iterable[String], cs: Charset = UTF8): Try[Path] = {
    write(path, lines, cs, StandardOpenOption.CREATE, StandardOpenOption.WRITE)
  }

  @varargs
  def write(path: Path, lines: Iterable[String], cs: Charset, options: OpenOption*): Try[Path] = Try {
    Files.write(path, lines.toSeq, cs, options: _*)
  }

  def writeAsync(path: Path, input: Array[Byte]): Future[Try[Int]] = {
    writeAsync(path, input, StandardOpenOption.CREATE, StandardOpenOption.WRITE)
  }

  @varargs
  @inline
  def writeAsync(path: Path, input: Array[Byte], options: OpenOption*): Future[Try[Int]] = future {
    Try {
      val fileChannel = Try(AsynchronousFileChannel.open(path, options: _*))
      fileChannel match {
        case Success(channel) =>
          try {
            val future = channel.write(ByteBuffer.wrap(input), 0)
            future.get(15, TimeUnit.MINUTES)
          } finally {
            channel.close()
          }
        case Failure(e) =>
          throw new RuntimeException("Fail to write to file.", e)
      }
    }
    //        val fileChannel = AsynchronousFileChannel.open(path, options: _*)
    //        try {
    //            val future = fileChannel.write(ByteBuffer.wrap(input), 0)
    //            future.get(15, TimeUnit.MINUTES)
    //        } finally {
    //            fileChannel.close()
    //        }
  }

  def writeAsync(path: Path, lines: Iterable[String], cs: Charset = UTF8): Future[Try[Int]] = {
    writeAsync(path, lines, cs, StandardOpenOption.CREATE, StandardOpenOption.WRITE)
  }

  @varargs
  def writeAsync(path: Path, lines: Iterable[String], cs: Charset, options: OpenOption*): Future[Try[Int]] = {
    val allText = lines.mkString(System.lineSeparator())
    writeAsync(path, cs.encode(allText).array(), options: _*)
  }
}
