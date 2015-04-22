package debop4s.core.io

import java.io._
import java.lang.{Iterable => JIterable}
import java.nio.ByteBuffer
import java.nio.channels.AsynchronousFileChannel
import java.nio.charset.Charset
import java.nio.file._
import java.nio.file.attribute.{BasicFileAttributes, FileAttribute}
import java.util
import java.util.concurrent.TimeUnit

import debop4s.core.utils.Closer._
import debop4s.core.utils.Streams
import org.slf4j.LoggerFactory

import scala.annotation.varargs
import scala.collection.JavaConversions._
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

  private[this] lazy val log = LoggerFactory.getLogger(getClass)

  val DEFUALT_BUFFER_SIZE: Int = 4096
  val UTF8: Charset = Charset.forName("UTF-8")

  @varargs
  def combine(base: String, others: String*): Path =
    Paths.get(base, others: _*)

  @varargs
  def combine(base: Path, others: String*): Path = {
    var result = base
    others.foreach {
      x => result = result.resolve(x)
    }
    result
  }

  def combine(base: Path, other: Path): Path =
    base.resolve(other)

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

  /**
   * 임시 디렉토리를 생성합니다.
   * @param deleteAtExit 프로세스 중단 시 임시 디렉토리를 삭제할 것인가 여부
   */
  def createTempDirectory(deleteAtExit: Boolean = true): File = {
    val file = File.createTempFile("temp", "dir")
    Try { file.delete() }
    file.mkdir()

    if (deleteAtExit) {
      // HINT: shutdown hook 를 추가한다
      Runtime.getRuntime.addShutdownHook(new Thread {
        override def run() {
          Files.delete(file.toPath)
        }
      })
    }
    file
  }

  /**
   * Create a temporary file from the given (resource) path. The
   * tempfile is deleted on JVM exit.
   *
   * Note, due to the usage of `File.deleteOnExit()` callers should
   * be careful using this as it can leak memory.
   * See http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4513817 for
   * example.
   *
   * @param path the resource-relative path to make a temp file from
   * @return the temp File object
   */
  def createTempFile(path: Path): File =
    createTempFile(getClass, path)

  /**
   * Create a temporary file from the given (resource) path. The
   * tempfile is deleted on JVM exit.
   *
   * Note, due to the usage of `File.deleteOnExit()` callers should
   * be careful using this as it can leak memory.
   * See http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4513817 for
   * example.
   *
   * @param clazz the `Class` to use for getting the resource.
   * @param path the resource-relative path to make a temp file from
   * @return the temp File object
   */
  def createTempFile(clazz: Class[_], path: Path): File = {
    Try(clazz.getResourceAsStream(path.toString)) match {
      case Success(stream) =>
        val tempPath = Files.createTempFile(path, "temp", "file", Seq[FileAttribute[_]](): _*)
        val file = tempPath.toFile
        file.deleteOnExit()
        using(new BufferedOutputStream(new FileOutputStream(file), 1 << 20)) { fos =>
          Streams.copy(stream, fos)
          fos.flush()
        }
        stream.close()
        file
      case Failure(ex) =>
        throw new FileNotFoundException(path.toString)
    }

    //    clazz.getResourceAsStream(path.toString) match {
    //      case null =>
    //        throw new FileNotFoundException(path.toString)
    //      case stream =>
    //        val tempPath = Files.createTempFile(path, "temp", "file", Seq[FileAttribute[_]](): _*)
    //        val file = tempPath.toFile
    //        file.deleteOnExit()
    //        using(new BufferedOutputStream(new FileOutputStream(file), 1 << 20)) { fos =>
    //          Streams.copy(stream, fos)
    //          fos.flush()
    //        }
    //        stream.close()
    //        file
    //    }
  }

  /**
   * 파일/디렉토리를 복사합니다.
   */
  @varargs
  def copy(src: Path, target: Path, options: CopyOption*): Unit = {
    Files.copy(src, target, options: _*)
  }

  /**
   * 비동기 방식으로 파일/디렉토리를 복사합니다.
   */
  @varargs
  def copyAsync(src: Path, target: Path, options: CopyOption*): Future[Unit] = Future {
    copy(src, target, options: _*)
  }

  @varargs
  def move(src: Path, dest: Path, options: CopyOption*): Unit = {
    Files.move(src, dest, options: _*)
  }

  @varargs
  def moveAsync(src: Path, dest: Path, options: CopyOption*): Future[Unit] = Future {
    move(src, dest, options: _*)
  }

  /**
   * 지정한 경로의 파일을 삭제합니다.
   */
  def delete(path: Path): Unit = {
    log.debug(s"delete file. path=$path")
    Files.delete(path)
  }

  /**
   * 지정한 경로의 파일이 존재한다면 삭제합니다.
   */
  def deleteIfExists(path: Path): Unit = {
    Files.deleteIfExists(path)
  }

  /**
   * 지정한 경로의 디렉토리를 삭제합니다.
   * @param dir 삭제할 디렉토리
   * @param deep 하위 디렉토리도 삭제할 것인가 여부
   */
  @inline
  def deleteDirectory(dir: Path, deep: Boolean = true): Unit = {
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

  def deleteDirectoryAsync(dir: Path, deep: Boolean = true): Future[Unit] = Future {
    deleteDirectory(dir, deep)
  }

  def exists(path: Path): Boolean =
    Files.exists(path, LinkOption.NOFOLLOW_LINKS)

  @varargs
  def exists(path: Path, linkOptions: LinkOption*): Boolean =
    Files.exists(path, linkOptions: _*)

  def readAllBytes(path: Path): Array[Byte] =
    Files.readAllBytes(path)

  def readAllBytesAsync(path: Path): Future[Array[Byte]] =
    readAllBytesAsync(path, StandardOpenOption.READ)

  /**
   * 지정한 경로의 파일 정보를 비동기 방식으로 모두 읽어드립니다.
   */
  @varargs
  @inline
  def readAllBytesAsync(path: Path, openOptions: OpenOption*): Future[Array[Byte]] = Future {
    assert(path != null)

    using(AsynchronousFileChannel.open(path, openOptions: _*)) { fileChannel =>
      val buffer = ByteBuffer.allocate(fileChannel.size().toInt)
      val result = fileChannel.read(buffer, 0)
      result.get()
      buffer.flip()
      buffer.array()
    }
  }

  def readAllLines(path: Path, cs: Charset = UTF8): util.List[String] = {
    log.debug(s"read all lines. path=$path, charset=$cs")
    Files.readAllLines(path, cs)
  }

  def readAllLines(is: InputStream): Try[util.List[String]] = {
    readAllLines(is, UTF8)
  }

  @inline
  def readAllLines(is: InputStream, cs: Charset): Try[util.List[String]] = Try {
    val lines = new util.ArrayList[String]()

    Try(new BufferedReader(new java.io.InputStreamReader(is, cs))) match {
      case Success(reader) =>
        var line = reader.readLine()
        while (line != null) {
          lines.add(line)
          line = reader.readLine()
        }
      case Failure(e) =>
        throw new RuntimeException("Cannot read file.", e)
    }
    lines
  }

  def readAllLines(input: Array[Byte]): Try[util.List[String]] = {
    readAllLines(input, UTF8)
  }

  def readAllLines(input: Array[Byte], cs: Charset): Try[util.List[String]] = {
    // scala 고유의 Option, Try 기능을 활용합니다.
    Try(new ByteArrayInputStream(input)) match {
      case Success(is) =>
        using(is) { stream => readAllLines(stream, cs) }
      case Failure(e) =>
        log.error("Fail to read bytes.", e)
        throw new RuntimeException("Fail to read bytes.", e)
    }
  }

  @varargs
  def readAllLinesAsync(path: Path, cs: Charset, openOptions: OpenOption*): Future[Try[util.List[String]]] =
    Future {
      val future = readAllBytesAsync(path, openOptions: _*)
      readAllLines(Await.result(future, 60 seconds), cs)
    }

  def readAllLinesAsync(is: InputStream): Future[Try[util.List[String]]] =
    readAllLinesAsync(is, UTF8)

  def readAllLinesAsync(is: InputStream, cs: Charset): Future[Try[util.List[String]]] =
    Future { readAllLines(is, cs) }

  def readAllLinesAsync(input: Array[Byte]): Future[Try[util.List[String]]] =
    readAllLinesAsync(input, UTF8)

  def readAllLinesAsync(input: Array[Byte], cs: Charset): Future[Try[util.List[String]]] =
    Future { readAllLines(input, cs) }

  def write(path: Path, input: Array[Byte]): Try[Path] =
    write(path, input, StandardOpenOption.CREATE, StandardOpenOption.WRITE)

  @varargs
  def write(path: Path, input: Array[Byte], options: OpenOption*): Try[Path] =
    Try { Files.write(path, input, options: _*) }

  def write(path: Path, lines: JIterable[String], cs: Charset = UTF8): Try[Path] =
    write(path, lines, cs, StandardOpenOption.CREATE, StandardOpenOption.WRITE)

  @varargs
  def write(path: Path, lines: JIterable[String], cs: Charset, options: OpenOption*): Try[Path] =
    Try { Files.write(path, lines, cs, options: _*) }

  def writeAsync(path: Path, input: Array[Byte]): Future[Int] =
    writeAsync(path, input, StandardOpenOption.CREATE, StandardOpenOption.WRITE)

  @varargs
  @inline
  def writeAsync(path: Path, input: Array[Byte], options: OpenOption*): Future[Int] =
    Future {
      Try(AsynchronousFileChannel.open(path, options: _*)) match {
        case Success(channel) =>
          using(channel) { fc =>
            val future = fc.write(ByteBuffer.wrap(input), 0)
            future.get(15, TimeUnit.MINUTES)
          }
        case Failure(e) =>
          throw new RuntimeException("Fail to write to file.", e)
      }
    }

  def writeAsync(path: Path, lines: JIterable[String], cs: Charset = UTF8): Future[Int] =
    writeAsync(path, lines, cs, StandardOpenOption.CREATE, StandardOpenOption.WRITE)

  @varargs
  def writeAsync(path: Path, lines: JIterable[String], cs: Charset, options: OpenOption*): Future[Int] = {
    val allText = lines.mkString(System.lineSeparator())
    writeAsync(path, cs.encode(allText).array(), options: _*)
  }
}
