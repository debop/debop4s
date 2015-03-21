package debop4s.data.mybatis.config

import java.io.Reader
import java.util
import java.util.Properties

import debop4s.data.mybatis.cache._
import debop4s.data.mybatis.mapping.{ JdbcType, Statement, T, TypeHandler }
import debop4s.data.mybatis.session.{ ExecutorType, SessionManager }
import org.apache.ibatis.builder.xml.XMLConfigBuilder
import org.apache.ibatis.io.Resources
import org.apache.ibatis.plugin.Interceptor
import org.apache.ibatis.session.{ Configuration => MBConfig, SqlSessionFactoryBuilder }
import org.slf4j.LoggerFactory

import scala.annotation.varargs
import scala.collection.JavaConverters._
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
      LOG.debug(s"Build MyBatis Configuration ...")

      val preConfig = new MBConfig()
      pre.sortBy(_.index).foreach(_.set(preConfig))

      val config = new Configuration(preConfig)
      pos.sortBy(_.index).foreach(_.set(config))
      config
    }

    def properties(props: (String, String)*) =
      set(0, pre) {
        _.getVariables.putAll(Map(props: _*).asJava)
      }

    def properties(props: Properties) =
      set(1, pre) {
        _.getVariables.putAll(props)
      }

    def properties(resource: String) =
      set(2, pre) {
        _.getVariables.putAll(Resources.getResourceAsProperties(resource))
      }

    def propertiesFromUrl(url: String) =
      set(3, pre) {
        _.getVariables.putAll(Resources.getUrlAsProperties(url))
      }

    def plugin(interceptor: Interceptor) =
      set(4, pre) { _.addInterceptor(interceptor) }

    def objectFactory(factory: ObjectFactory) =
      set(5, pre) { _.setObjectFactory(factory) }

    def objectWrapperFactory(owf: ObjectWrapperFactory) =
      set(6, pre) { _.setObjectWrapperFactory(owf) }

    def autoMappingBehavior(behavior: AutoMappingBehavior) =
      set(8, pre) { _.setAutoMappingBehavior(behavior.unwrap) }

    def cacheSupport(enabled: Boolean) =
      set(9, pre) { _.setCacheEnabled(enabled) }

    def lazyLoadingSupport(enabled: Boolean) =
      set(10, pre) { _.setLazyLoadingEnabled(enabled) }

    def aggressiveLazyLoading(enabled: Boolean) =
      set(11, pre) { _.setAggressiveLazyLoading(enabled) }

    def multipleResultSetsSupport(enabled: Boolean) =
      set(12, pre) { _.setMultipleResultSetsEnabled(enabled) }

    def useColumnLabel(enabled: Boolean) =
      set(13, pre) { _.setUseColumnLabel(enabled) }

    def useGeneratedKeys(enabled: Boolean) =
      set(14, pre) { _.setUseGeneratedKeys(enabled) }

    def defaultExecutorType(executorType: ExecutorType) =
      set(15, pre) { _.setDefaultExecutorType(executorType.unwrap) }

    def defaultStatementTimeout(timeout: Int = -1) =
      set(16, pre) { _.setDefaultStatementTimeout(timeout) }

    def mapUnderscoreToCamelCase(enabled: Boolean) =
      set(17, pre) { _.setMapUnderscoreToCamelCase(enabled) }

    def safeRowBoundsSupport(enabled: Boolean) =
      set(18, pre) { _.setSafeRowBoundsEnabled(enabled) }

    def localCacheScope(localCacheScope: LocalCacheScope) =
      set(19, pre) { _.setLocalCacheScope(localCacheScope.unwrap) }

    def jdbcTypeForNull(jdbcType: JdbcType) =
      set(20, pre) { _.setJdbcTypeForNull(jdbcType.unwrap) }

    def lazyLodTriggerMethods(names: util.Set[String]) =
      set(21, pre) { _.setLazyLoadTriggerMethods(names) }

    def environment(id: String, transactionFactory: TransactionFactory, dataSource: javax.sql.DataSource) =
      set(24, pre) {
        _.setEnvironment(new Environment(id, transactionFactory, dataSource).unwrap)
      }

    def databaseIdProvider(provider: DatabaseIdProvider) =
      set(25, pre) { c =>
        c.setDatabaseId(provider.getDatabaseId(c.getEnvironment.getDataSource))
      }

    def typeHandler(jdbcType: JdbcType, handler: (T[_], TypeHandler[_])) =
      set(26, pre) {
        _.getTypeHandlerRegistry.register(handler._1.raw, jdbcType.unwrap, handler._2)
      }

    def typeHandler(handler: (T[_], TypeHandler[_])) =
      set(26, pre) {
        _.getTypeHandlerRegistry.register(handler._1.raw, handler._2)
      }

    def typeHandler(handler: TypeHandler[_]) =
      set(26, pre) {
        _.getTypeHandlerRegistry.register(handler)
      }

    def namespace(name: String)(f: ConfigurationSpace => Unit) =
      set(0, pos) { c =>
        f(new ConfigurationSpace(c.configuration, name))
      }

    @varargs
    def statements(s: Statement*) = set(1, pos) { _ ++= s }

    def statements(s: java.lang.Iterable[Statement]) = set(1, pos) { _ ++= s.asScala.toList }

    def mappers(mappers: {def bind: Seq[Statement]}*) =
      set(1, pos) { c => mappers.foreach(m => c ++= m) }

    def cacheRef(that: ConfigurationSpace) =
      set(2, pos) { _.cacheRef(that) }

    def cache(impl: T[_ <:Cache] = DefaultCache,
               eviction: T[_ <: Cache] = Eviction.LRU,
               flushInterval: Long = -1,
               size:Int = -1,
               readWrite:Boolean = true,
               props:Properties = null): Unit = {
      set(2, pos) {
        _.cache(impl, eviction, flushInterval, size, readWrite, props)
      }
    }

    // PENDING FOR mybatis 3.1.1+ ==================================================

    // TODO (3.1.1) def proxyFactory(factory: ProxyFactory) = set( 7, pre) { _.setProxyFactory(factory) }
    // TODO (3.1.1) def safeResultHandlerSupport(enabled : Boolean) = set(22, pre) { _.setSafeResultHandlerEnabled(enabled) }
    // TODO (3.1.1) def defaultScriptingLanguage(driver : T[_]) = set(23, pre) { _.setDefaultScriptingLanguage(driver) }
  }
}
