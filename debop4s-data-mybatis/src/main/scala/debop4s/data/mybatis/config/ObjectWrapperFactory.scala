package debop4s.data.mybatis.config

import java.util

import org.apache.ibatis.reflection.MetaObject
import org.apache.ibatis.reflection.property.PropertyTokenizer
import org.apache.ibatis.reflection.wrapper.ObjectWrapper

import scala.collection.JavaConverters._
import scala.collection.mutable

abstract class CollectionObjectWrapper extends org.apache.ibatis.reflection.wrapper.ObjectWrapper {

  override def get(prop: PropertyTokenizer): AnyRef = null
  override def set(prop: PropertyTokenizer, value: AnyRef): Unit = {}
  override def findProperty(name: String, useCamelCaseMapping: Boolean): String = null
  override def getGetterNames: Array[String] = null
  override def getSetterNames: Array[String] = null
  override def getGetterType(name: String): Class[_] = null
  override def getSetterType(name: String): Class[_] = null
  override def hasGetter(name: String): Boolean = false
  override def hasSetter(name: String): Boolean = false

  override def instantiatePropertyValue(name: String, prop: PropertyTokenizer, objectFactory: ObjectFactory): MetaObject = null
  override def isCollection: Boolean = true
}

class ArrayBufferWrapper(buffer: mutable.ArrayBuffer[AnyRef]) extends CollectionObjectWrapper {
  override def add(element: scala.AnyRef): Unit = buffer += element
  override def addAll[E](elements: util.List[E]): Unit = buffer ++= elements.asInstanceOf[util.List[AnyRef]].asScala
}

class HashSetWrapper(set: mutable.HashSet[AnyRef]) extends CollectionObjectWrapper {
  override def add(element: scala.AnyRef): Unit = set += element.asInstanceOf[AnyRef]
  override def addAll[E](elements: util.List[E]): Unit = set ++= elements.asInstanceOf[util.List[AnyRef]].asScala
}

class DefaultObjectWrapperFactory extends ObjectWrapperFactory {
  override def hasWrapperFor(obj: scala.Any): Boolean = obj match {
    case o: mutable.ArrayBuffer[_] => true
    case o: mutable.HashSet[_] => true
    case _ => false

  }
  override def getWrapperFor(metaObject: MetaObject, obj: AnyRef): ObjectWrapper = obj match {
    case o: mutable.ArrayBuffer[AnyRef] => new ArrayBufferWrapper(o)
    case o: mutable.HashSet[AnyRef] => new HashSetWrapper(o)
    case _ =>
      throw new IllegalArgumentException(s"Type not supported: ${obj.getClass.getSimpleName}")
  }
}

