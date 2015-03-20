package debop4s.data.mybatis.config

import java.io.Reader
import java.util.Properties

import debop4s.data.mybatis.cache._
import debop4s.data.mybatis.mapping.{Statement, T, TypeHandler}
import debop4s.data.mybatis.session.SessionManager
import org.apache.ibatis.builder.xml.XMLConfigBuilder
import org.apache.ibatis.io.Resources
import org.apache.ibatis.session.{Configuration => MBConfig, SqlSessionFactoryBuilder}
import org.slf4j.LoggerFactory

import scala.collection.mutable

/**
 * MyBatis Configuration
 * @author sunghyouk.bae@gmail.com at 15. 3. 20.
 */
class Configuration(private val configuration: MBConfig) {

  private val LOG = LoggerFactory.getLogger(getClass)

  if (configuration.getObjectFactory.getClass == classOf[org.apache.ibatis.reflection.factory.DefaultObjectFactory]) {
    configuration.setObjectFactory(new DefaultObjectFactory())
  }
  if (configuration.getObjectWrapperFactory.getClass == classOf[org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory]) {
    configuration.setObjectWrapperFactory(new DefaultObjectWrapperFactory)
  }

  registerCommonOptionTypeHandlers

  lazy val defaultSpace = new ConfigurationSpace(configuration, "_DEFAULT_")


  def addSpace(name: String)(f: ConfigurationSpace => Unit): this.type = {
    val space = new ConfigurationSpace(configuration, name)
    f(space)
    this
  }


  def +=(s: Statement) = defaultSpace += s

  def ++=(ss: Seq[Statement]) = defaultSpace ++= ss

  def ++=(mapper: {def bind: Seq[Statement]}) = defaultSpace ++= mapper

  /**
   * Adds cache support to this space.
   * @param impl Cache implementation class
   * @param eviction cache eviction policy (LRU,FIFO,WEAK,SOFT)
   * @param flushInterval any positive integer in milliseconds.
   *                      The default is not set, thus no flush interval is used and the cache is only flushed by calls to statements.
   * @param size max number of objects that can live in the cache. Default is 1024
   * @param readWrite A read-only cache will return the same instance of the cached object to all callers.
   *                  Thus such objects should not be modified.  This offers a significant performance advantage though.
   *                  A read-write cache will return a copy (via serialization) of the cached object,
   *                  this is slower, but safer, and thus the default is true.
   * @param props implementation specific properties.
   */
  def cache(impl: T[_ <: Cache] = DefaultCache,
            eviction: T[_ <: Cache] = Eviction.LRU,
            flushInterval: Long = -1L,
            size: Int = -1,
            readWrite: Boolean = true,
            props: Properties = null) =
    defaultSpace.cache(impl, eviction, flushInterval, size, readWrite, props)

  /** Reference to an external cache */
  def cacheRef(that: ConfigurationSpace) = defaultSpace.cacheRef(that)

  /** Builds a Session Manager */
  def createPersistenceContext: SessionManager = {
    val builder = new SqlSessionFactoryBuilder
    new SessionManager(builder.build(configuration))
  }

  private def registerOptionTypeHandler[T <: Option[_]](h: TypeHandler[T], jdbcTypes: Seq[org.apache.ibatis.`type`.JdbcType]) = {

    val registry = configuration.getTypeHandlerRegistry
    val cls = classOf[Option[_]]
    jdbcTypes.foreach { jdbcType =>
      registry.register(cls, jdbcType, h)
    }
  }

  private def registerCommonOptionTypeHandlers = {
    import debop4s.data.mybatis.mapping.OptionTypeHandler
    import debop4s.data.mybatis.mapping.TypeHandlers._
    import org.apache.ibatis.`type`.JdbcType._
    import org.apache.ibatis.`type`._

    registerOptionTypeHandler(new OptionBooleanTypeHandler, Seq(BOOLEAN, BIT))
    registerOptionTypeHandler(new OptionByteTypeHandler, Seq(TINYINT))
    registerOptionTypeHandler(new OptionShortTypeHandler, Seq(SMALLINT))
    registerOptionTypeHandler(new OptionIntegerTypeHandler, Seq(INTEGER))
    registerOptionTypeHandler(new OptionFloatTypeHandler, Seq(FLOAT))
    registerOptionTypeHandler(new OptionDoubleTypeHandler, Seq(DOUBLE))
    registerOptionTypeHandler(new OptionLongTypeHandler, Seq(BIGINT))
    registerOptionTypeHandler(new OptionStringTypeHandler, Seq(VARCHAR, CHAR))
    registerOptionTypeHandler(new OptionClobTypeHandler, Seq(CLOB, LONGVARCHAR))
    registerOptionTypeHandler(new OptionNStringTypeHandler, Seq(NVARCHAR, NCHAR))
    registerOptionTypeHandler(new OptionNClobTypeHandler, Seq(NCLOB))
    registerOptionTypeHandler(new OptionBigDecimalTypeHandler, Seq(REAL, DECIMAL, NUMERIC))
    registerOptionTypeHandler(new OptionBlobTypeHandler, Seq(BLOB, LONGVARBINARY))
    registerOptionTypeHandler(new OptionDateTypeHandler, Seq(DATE))
    registerOptionTypeHandler(new OptionTimeTypeHandler, Seq(TIME))
    registerOptionTypeHandler(new OptionTimestampTypeHandler, Seq(TIMESTAMP))
    registerOptionTypeHandler(new OptionTypeHandler(new UnknownTypeHandler(configuration.getTypeHandlerRegistry)), Seq(OTHER))
  }
}

/**
 * Companion Object of [[Configuration]]
 */
object Configuration {

  private val LOG = LoggerFactory.getLogger(getClass)

  def apply(reader: Reader): Configuration = {
    val builder = new XMLConfigBuilder(reader)
    new Configuration(builder.parse)
  }

  def apply(reader: Reader, env: String): Configuration = {
    val builder = new XMLConfigBuilder(reader, env)
    new Configuration(builder.parse)
  }

  def apply(reader: Reader, env: String, properties: Properties): Configuration = {
    val builder = new XMLConfigBuilder(reader, env, properties)
    new Configuration(builder.parse)
  }

  def apply(path: String): Configuration = {
    apply(Resources.getResourceAsReader(path))
  }

  def apply(path: String, env: String): Configuration = {
    apply(Resources.getResourceAsReader(path), env)
  }

  def apply(path: String, env: String, properties: Properties): Configuration = {
    apply(Resources.getResourceAsReader(path), env, properties)
  }

  def apply(env: Environment): Configuration = {
    new Configuration(new MBConfig(env.unwrap))
  }

  def apply(builder: Builder): Configuration = builder.build


  class Builder {

    import scala.collection.mutable.ArrayBuffer

    /**
     * Reference to self. Support for optional notation.
     */
    protected val >> = this

    /**
     * Mutable hidden state, discarded after construction
     */
    private val pre = new mutable.ArrayBuffer[ConfigElem[MBConfig]]

    /**
     * Mutable hidden state, discarded after construction
     */
    private val pos = new mutable.ArrayBuffer[ConfigElem[Configuration]]()

    /** Configuration Element */
    private abstract class ConfigElem[A] {
      val index: Int
      def set(config: A): Unit
    }

    /** Ordered deferred setter */
    private def set[A](i: Int, e: ArrayBuffer[ConfigElem[A]])(f: A => Unit): Unit = {
      e += new ConfigElem[A] {
        override val index = i
        def set(c: A) = f(c)
      }
    }

    /** Build the configuration object */
    private[Configuration] def build(): Configuration = {
      val preConfig = new MBConfig()
      pre.sortBy(_.index).foreach(_.set(preConfig))

      val config = new Configuration(preConfig)
      pos.sortBy(_.index).foreach(_.set(config))
      config
    }
  }
}
