package debop4s.data.slick3

import org.junit.Assert

import scala.reflect.ClassTag

/**
 * package
 * @author sunghyouk.bae@gmail.com
 */
package object tests {

  implicit class CollectionAssertionExtensionMethods[T](v: TraversableOnce[T]) {
    private[this] val cln = getClass.getName
    private[this] def fixStack(f: => Unit): Unit = try f catch {
      case ex: AssertionError =>
        ex.setStackTrace(ex.getStackTrace.iterator.filterNot(_.getClassName.startsWith(cln)).toArray)
        throw ex
    }

    def shouldAllMatch(f: PartialFunction[T, _]) = v.foreach { x =>
      if (!f.isDefinedAt(x)) fixStack(Assert.fail("Value does not match expected shape: " + x))
    }
  }
}
