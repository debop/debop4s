package debop4s.data.mybatis.mapping

import java.util

import debop4s.data.mybatis.session.{ ResultContext, ResultHandlerDelegator, RowBounds, Session }

import scala.reflect._

/**
 * Base class for all Select statements.
 * @author sunghyouk.bae@gmail.com at 15. 3. 20.
 */
sealed trait Select extends Statement {

  /**
   * A reference to an external resultMap.
   * Result maps are the most powerful feature of MyBatis, and with a good understanding of them,
   * many difficult mapping cases can be solved.
   */
  var resultMap: ResultMap[_] = null

  /**
   * Any one of FORWARD_ONLY|SCROLL_SENSITIVE|SCROLL_INSENSITIVE. Default FORWARD_ONLY.
   */
  var resultSetType: ResultSetType = ResultSetType.FORWARD_ONLY

  var fetchSize: Int = -1

  var useCache: Boolean = true

  flushCache = false

  def resultTypeClass: Class[_]

}

/**
 * Query for a list of objects.
 *
 * == Details ==
 * This class defines a function: (=> List[Result])
 *
 * == Sample code ==
 * {{{
 *   val findAll = new SelectList[Person] {
 *     def xsql = "SELECT * FROM person ORDER BY name"
 *   }
 *
 *   // Configuration etc .. omitted ..
 *
 *   // Then use it
 *   db.readOnly {
 *     val list = findAll()
 *     ...
 *   }
 *
 * }}}
 * @tparam R retult type
 */
abstract class SelectList[R: Manifest] extends Select with SQLFunction0[util.List[R]] {

  def parameterTypeClass = classOf[Nothing]
  def resultTypeClass = manifest[R].runtimeClass

  def apply()(implicit s: Session): util.List[R] = execute {
    s.selectList[R](fqi.id)
  }

  def handle(callback: ResultContext => Unit)(implicit s: Session): Unit = execute {
    s.select(fqi.id, new ResultHandlerDelegator(callback))
  }
}

/** Query for a list of objects using the input parameter.
  *
  * == Details ==
  * This class defines a function: (Param => List[Result])
  *
  * == Sample code ==
  * {{{
  *   val findByName = new SelectListBy[String,Person] {
  *     def xsql = "SELECT * FROM person WHERE name LIKE #{name}"
  *   }
  *
  *   // Configuration etc .. omitted ..
  *
  *   // Then use it
  *   db.readOnly {
  *     val list = findByName("John%")
  *     ...
  *   }
  *
  * }}}
  * @tparam P input parameter type
  * @tparam R retult type
  */
abstract class SelectListBy[P: Manifest, R: Manifest] extends Select with SQLFunction1[P, util.List[R]] {

  def parameterTypeClass = manifest[P].runtimeClass
  def resultTypeClass = manifest[R].runtimeClass

  def apply(param: P)(implicit s: Session): util.List[R] = execute {
    s.selectList[P, R](fqi.id, param)
  }

  def handle(param: P, callback: ResultContext => Unit)(implicit s: Session): Unit = execute {
    s.select(fqi.id, param, new ResultHandlerDelegator(callback))
  }
}

/** Query for a list of objects with RowBounds.
  *
  * == Details ==
  * This class defines a function: (RowBounds => List[Result])
  *
  * == Sample code ==
  * {{{
  *   val findAll = new SelectListPage[Person] {
  *     def xsql = "SELECT * FROM person ORDER BY name"
  *   }
  *
  *   // Configuration etc .. omitted ..
  *
  *   // Then use it
  *   db.readOnly {
  *     val list = findAll(RowBounds(100, 20))
  *     ...
  *   }
  *
  * }}}
  * @tparam R retult type
  */
abstract class SelectListPage[R: Manifest] extends Select with SQLFunction0[util.List[R]] {

  def parameterTypeClass = classOf[Nothing]
  def resultTypeClass = manifest[R].runtimeClass

  def apply(rowBounds: RowBounds)(implicit s: Session): util.List[R] = execute {
    s.selectList[Null, R](fqi.id, null, rowBounds)
  }

  def handle(rowBounds: RowBounds, callback: ResultContext => Unit)(implicit s: Session): Unit = execute {
    s.select(fqi.id, rowBounds, new ResultHandlerDelegator(callback))
  }

}


/** Query for a list of objects with RowBounds and one input parameter.
  *
  * == Details ==
  * This class defines a function: ((Param, RowBounds) => List[Result])
  *
  * == Sample code ==
  * {{{
  *   val findByName = new SelectListPageBy[String,Person] {
  *     def xsql = "SELECT * FROM person WHERE name LIKE #{name}"
  *   }
  *
  *   // Configuration etc .. omitted ..
  *
  *   // Then use it
  *   db.readOnly {
  *     val list = findByName("John%", RowBounds(100, 20))
  *     ...
  *   }
  *
  * }}}
  * @tparam P input parameter type
  * @tparam R retult type
  */
abstract class SelectListPageBy[P: Manifest, R: Manifest] extends Select with SQLFunction2[P, RowBounds, util.List[R]] {

  override def parameterTypeClass = manifest[P].runtimeClass
  override def resultTypeClass = manifest[R].runtimeClass

  override def apply(param: P, rowBounds: RowBounds)(implicit s: Session): util.List[R] = execute {
    s.selectList[P, R](fqi.id, param, rowBounds)
  }

  def handle(param: P, rowBounds: RowBounds, callback: ResultContext => Unit)(implicit s: Session): Unit = execute {
    s.select(fqi.id, param, rowBounds, new ResultHandlerDelegator(callback))
  }
}

/** Query for a single object.
  *
  * == Details ==
  * This class defines a function: (=> Result)
  *
  * == Sample code ==
  * {{{
  *   val find = new SelectOne[Person] {
  *     def xsql = "SELECT * FROM person WHERE id = 1"
  *   }
  *
  *   // Configuration etc .. omitted ..
  *
  *   // Then use it
  *   db.readOnly {
  *     val p = find()
  *     ...
  *   }
  *
  * }}}
  * @tparam R retult type
  */
abstract class SelectOne[R: Manifest] extends Select with SQLFunction0[Option[R]] {

  def parameterTypeClass = classOf[Nothing]
  def resultTypeClass = manifest[R].runtimeClass

  def apply()(implicit s: Session): Option[R] = execute {
    Option(s.selectOne[R](fqi.id))
  }
}

/** Query for a single object using an input parameter.
  *
  * == Details ==
  * This class defines a function: (Param => Result)
  *
  * == Sample code ==
  * {{{
  *   val find = new SelectOneBy[Int,Person] {
  *     def xsql = "SELECT * FROM person WHERE id = #{id}"
  *   }
  *
  *   // Configuration etc .. omitted ..
  *
  *   // Then use it
  *   db.readOnly {
  *     val p = find(1)
  *     ...
  *   }
  *
  * }}}
  * @tparam P input parameter type
  * @tparam R retult type
  */
abstract class SelectOneBy[P: Manifest, R: Manifest] extends Select with SQLFunction1[P, Option[R]] {

  override def parameterTypeClass = manifest[P].runtimeClass
  override def resultTypeClass = manifest[R].runtimeClass

  def apply(p: P)(implicit s: Session): Option[R] = execute {
    Option(s.selectOne[P, R](fqi.id, p))
  }
}

/** Query for a Map of objects.
  *
  * == Details ==
  * This class defines a function: (=> Map[ResultKey, ResultValue])
  *
  * == Sample code ==
  * {{{
  *   val peopleMapById = new SelectMap[Long,Person](mapKey="id") {
  *     def xsql = "SELECT * FROM person"
  *   }
  *
  *   // Configuration etc .. omitted ..
  *
  *   // Then use it
  *   db.readOnly {
  *     val people = peopleMapById()
  *     val p = people(5)
  *     ...
  *   }
  *
  * }}}
  * @tparam RK map Key type
  * @tparam RV map Value type
  * @param mapKey Property to be used as map key
  */
abstract class SelectMap[RK, RV: Manifest](mapKey: String) extends Select with SQLFunction0[util.Map[RK, RV]] {

  def parameterTypeClass = classOf[Nothing]
  def resultTypeClass = manifest[RV].runtimeClass

  def apply()(implicit s: Session): util.Map[RK, RV] = execute {
    s.selectMap[RK, RV](fqi.id, mapKey)
  }
}

/** Query for a Map of objects using an input parameter.
  *
  * == Details ==
  * This class defines a function: (Param => Map[ResultKey, ResultValue])
  *
  * == Sample code ==
  * {{{
  *   val peopleMapById = new SelectMapBy[String,Long,Person](mapKey="id") {
  *     def xsql = "SELECT * FROM person WHERE name LIKE #{name}"
  *   }
  *
  *   // Configuration etc .. omitted ..
  *
  *   // Then use it
  *   db.readOnly {
  *     val people = peopleMapById("John%")
  *     val p = people(3)
  *     ...
  *   }
  *
  * }}}
  * @tparam P input parameter type
  * @tparam RK map Key type
  * @tparam RV map Value type
  * @param mapKey Property to be used as map key
  */
abstract class SelectMapBy[P: Manifest, RK, RV: Manifest](mapKey: String) extends Select with SQLFunction1[P, util.Map[RK, RV]] {

  override def parameterTypeClass = manifest[P].runtimeClass

  override def resultTypeClass = manifest[RV].runtimeClass

  override def apply(p: P)(implicit s: Session): util.Map[RK, RV] = execute {
    s.selectMap[P, RK, RV](fqi.id, p, mapKey)
  }
}

/** Query for a Map of objects with RowBounds.
  *
  * == Details ==
  * This class defines a function: (RowBounds => Map[ResultKey, ResultValue])
  *
  * == Sample code ==
  * {{{
  *   val peopleMapById = new SelectMapPage[Long,Person](mapKey="id") {
  *     def xsql = "SELECT * FROM person"
  *   }
  *
  *   // Configuration etc .. omitted ..
  *
  *   // Then use it
  *   db.readOnly {
  *     val people = peopleMapById(RowBounds(100,20))
  *     val p = people(3)
  *     ...
  *   }
  *
  * }}}
  * @tparam RK map Key type
  * @tparam RV map Value type
  * @param mapKey Property to be used as map key
  */
abstract class SelectMapPage[RK, RV: Manifest](mapKey: String) extends Select with SQLFunction1[RowBounds, util.Map[RK, RV]] {

  override def parameterTypeClass = classOf[Nothing]
  override def resultTypeClass = manifest[RV].runtimeClass

  override def apply(rowBounds: RowBounds)(implicit s: Session): util.Map[RK, RV] = execute {
    s.selectMap[Null, RK, RV](fqi.id, null, mapKey, rowBounds)
  }
}

/** Query for a Map of objects with RowBounds and one input parameter.
  *
  * == Details ==
  * This class defines a function: ((Param, RowBounds) => Map[ResultKey, ResultValue])
  *
  * == Sample code ==
  * {{{
  *   val peopleMapById = new SelectMapPageBy[String,Long,Person](mapKey="id") {
  *     def xsql = "SELECT * FROM person WHERE name LIKE #{name}"
  *   }
  *
  *   // Configuration etc .. omitted ..
  *
  *   // Then use it
  *   db.readOnly {
  *     val people = peopleMapById("John%", RowBounds(100,20))
  *     val p = people(3)
  *     ...
  *   }
  *
  * }}}
  * @tparam P input parameter type
  * @tparam RK map Key type
  * @tparam RV map Value type
  * @param mapKey Property to be used as map key
  */
abstract class SelectMapPageBy[P: Manifest, RK, RV: Manifest](mapKey: String) extends Select with SQLFunction2[P, RowBounds, util.Map[RK, RV]] {

  def parameterTypeClass = manifest[P].runtimeClass
  def resultTypeClass = manifest[RV].runtimeClass

  def apply(p: P, rowBounds: RowBounds)(implicit s: Session): util.Map[RK, RV] = execute {
    s.selectMap[P, RK, RV](fqi.id, p, mapKey, rowBounds)
  }
}