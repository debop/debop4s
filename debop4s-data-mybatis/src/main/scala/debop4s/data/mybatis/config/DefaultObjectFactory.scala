package debop4s.data.mybatis.config

import java.util
import java.util.Properties

import org.apache.ibatis.reflection.ReflectionException

import scala.collection.mutable
import scala.util.control.NonFatal

/**
 * DefaultObjectFactory
 * @author sunghyouk.bae@gmail.com at 15. 3. 20.
 */
class DefaultObjectFactory extends ObjectFactory {

  val cache = new mutable.HashMap[CacheKey, java.lang.reflect.Constructor[_]]
  override def create[T](clazz: Class[T]): T = create(clazz, null, null)
  override def create[T](clazz: Class[T],
                         constructorArgTypes: util.List[Class[_]],
                         constructorArgs: util.List[AnyRef]): T = {
    val classToCreate = resolveInterface(clazz)
    instanciateClass[T](classToCreate, constructorArgTypes, constructorArgs)
  }
  override def setProperties(properties: Properties): Unit = {}

  override def isCollection[T](clazz: Class[T]): Boolean = {
    classOf[scala.collection.Seq[_]].isAssignableFrom(clazz) ||
    classOf[scala.collection.Set[_]].isAssignableFrom(clazz)
  }

  private def instanciateClass[T](clazz: Class[_], constructorArgTypes: util.List[Class[_]], constructorArgs: util.List[AnyRef]): T = {
    val argTypes =
      if (constructorArgTypes != null) constructorArgTypes.toArray[Class[_]](new Array[Class[_]](constructorArgTypes.size))
      else null

    val constructor = getConstructor(clazz, argTypes)

    val argValues =
      if (constructorArgs != null) constructorArgs.toArray[AnyRef](new Array[AnyRef](constructorArgs.size))
      else null

    try {
      if (argTypes == null || argValues == null)
        constructor.newInstance().asInstanceOf[T]
      else
        constructor.newInstance(argValues: _*).asInstanceOf[T]
    } catch {
      case NonFatal(e) =>
        val types = if (argTypes == null) "" else argTypes.map(_.getSimpleName).reduceLeft(_ + "," + _)
        val values = if (argValues == null) "" else argValues.map(String.valueOf(_)).reduceLeft(_ + "," + _)
        throw new ReflectionException(s"Error instanciating ${clazz.getSimpleName} with " +
                                      s"invalid types ($types) or values($values)}. Cause: ${e.getMessage}", e)
    }
  }

  private def resolveInterface[T](clazz: Class[T]): Class[_] = {

    // Java Collections
    if (clazz == classOf[java.util.List[_]]) classOf[java.util.LinkedList[_]]
    else if (clazz == classOf[java.util.Collection[_]]) classOf[java.util.LinkedList[_]]
    else if (clazz == classOf[java.util.Map[_, _]]) classOf[java.util.HashMap[_, _]]
    else if (clazz == classOf[java.util.SortedSet[_]]) classOf[java.util.TreeSet[_]]
    else if (clazz == classOf[java.util.Set[_]]) classOf[java.util.HashSet[_]]

    // Scala Collections
    else if (clazz == classOf[scala.collection.Seq[_]]) classOf[scala.collection.mutable.ArrayBuffer[_]]
    else if (clazz == classOf[scala.collection.Map[_, _]]) classOf[scala.collection.mutable.HashMap[_, _]]
    else if (clazz == classOf[scala.collection.Set[_]]) classOf[scala.collection.mutable.HashSet[_]]

    else clazz
  }

  def getConstructor(clazz: Class[_], args: Array[Class[_]]): java.lang.reflect.Constructor[_] = {
    cache.getOrElseUpdate(new CacheKey(clazz, args), {
      try {
        if (args == null) {
          val constructor = clazz.getDeclaredConstructor(Seq(): _*)
          if (!constructor.isAccessible) {
            constructor.setAccessible(true)
          }
          constructor
        } else {
          val constructor = clazz.getDeclaredConstructor(args: _*)
          if (!constructor.isAccessible) {
            constructor.setAccessible(true)
          }
          constructor
        }
      } catch {
        case NonFatal(e) =>
          val types = if (args == null) "" else args.map(_.getSimpleName).reduceLeft(_ + "," + _)
          throw new ReflectionException(s"Error instanciating ${clazz.getSimpleName} " +
                                        s"with invalid types ($types). Cause: ${e.getMessage}", e)
      }
    })
  }

  sealed class CacheKey(clazz: Class[_], args: Array[Class[_]]) {
    val _hc: Int = {
      var code = clazz.hashCode()
      if (args != null) {
        args.foreach { arg => code = code * 41 + arg.hashCode()}
      }
      code
    }

    override def hashCode = _hc
    override def equals(that: Any): Boolean =
      that != null &&
      that.getClass == classOf[CacheKey] &&
      that.asInstanceOf[CacheKey].hashCode == this.hashCode
  }


}
