package kr.debop4s.core.utils

import kr.debop4s.core.logging.Logger
import scala.reflect._

/**
 * kr.debop4s.core.tools.Reflects
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 12. 오후 5:23
 */
class Reflects {

    implicit lazy val log = Logger(getClass)

    /**
     * Java의 Primitive 수형에 대한 Box된 Scala 클래스를 Unbox한 Java Primitive 수형 타입을 구합니다.
     *
     * @param x 스칼라 수형
     * @return  Java의 Primitive Type
     */
    @inline
    def asJavaClass(x: Any): Class[_] = x match {
        case x: scala.Boolean => java.lang.Boolean.TYPE
        case x: scala.Char => java.lang.Character.TYPE
        case x: scala.Byte => java.lang.Byte.TYPE
        case x: scala.Short => java.lang.Short.TYPE
        case x: scala.Int => java.lang.Integer.TYPE
        case x: scala.Long => java.lang.Long.TYPE
        case x: scala.Float => java.lang.Float.TYPE
        case x: scala.Double => java.lang.Double.TYPE
        case _ => x.getClass
    }

    def tagToClass[T](tag: ClassTag[T]): Class[T] = {
        tag.runtimeClass.asInstanceOf[Class[T]]
    }

    /**
     * Generic 수형에 대해 Runtime 의 수형을 구합니다.
     * 사용 시에는 getRuntimeClass[X] 만 호출하면 됩니다.
     * {{{
     *     val clazz = getRuntimeClass[classTag[T]]
     * }}}
     */
    def getRuntimeClass[T](implicit tag: ClassTag[T]) =
        tag.runtimeClass.asInstanceOf[Class[T]]

    def newInstance[T](implicit tag: ClassTag[T]): T =
        tag.runtimeClass.newInstance().asInstanceOf[T]

    /**
     * Generic 수형의 클래스를 생성합니다.
     *
     * {{{
     *     val instance = ScalaReflects.newInstace[MyClass](1,2,3)
     * }}}
     *
     * @param initArgs 생성자를 위한 인자
     * @tparam T 인스턴스 수형
     * @return 생성된 인스턴스
     */
    def newInstance[T](initArgs: Any*)(implicit tag: ClassTag[T]): T = {
        log.trace(s"인스턴스를 생성합니다. type=[${tag.runtimeClass.getName}]")

        if (initArgs == null || initArgs.length == 0)
            return newInstance[T]
        // classTag[T].runtimeClass.newInstance().asInstanceOf[T]

        val parameterTypes = initArgs.map(x => asJavaClass(x)).toArray
        val constructor = getRuntimeClass[T].getConstructor(parameterTypes: _*)

        constructor.newInstance(initArgs.map(_.asInstanceOf[AnyRef]): _*).asInstanceOf[T]
    }

    /**
     * Generic 수형의 클래스를 생성합니다.
     *
     * @param initArgs 생성자를 위한 인자
     * @tparam T 인스턴스 수형
     * @return 생성된 인스턴스
     */
    def newInstanceWithTypes[T](initArgs: Any*)(initArgsTypes: Class[_]*)(implicit tag: ClassTag[T]): T = {
        if (initArgs == null || initArgs.length == 0)
            return newInstance[T]

        log.trace(s"인스턴스를 생성합니다. type=[${classTag[T].runtimeClass.getName}]")

        val parameterTypes =
            if (initArgsTypes != null) initArgsTypes.toArray
            else initArgs.map(asJavaClass).toArray

        val constructor = classTag[T].runtimeClass.getConstructor(parameterTypes: _*)

        constructor.newInstance(initArgs.map(_.asInstanceOf[AnyRef]): _*).asInstanceOf[T]
    }

}
