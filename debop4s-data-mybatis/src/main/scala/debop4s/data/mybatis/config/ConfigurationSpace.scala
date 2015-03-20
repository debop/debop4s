package debop4s.data.mybatis.config

import java.util
import java.util.Properties

import debop4s.data.mybatis.cache._
import debop4s.data.mybatis.mapping._
import org.apache.ibatis.builder.MapperBuilderAssistant
import org.apache.ibatis.cache.Cache
import org.apache.ibatis.executor.keygen.{Jdbc3KeyGenerator, KeyGenerator => MBKeyGenerator, NoKeyGenerator, SelectKeyGenerator}
import org.apache.ibatis.mapping.{Discriminator, ResultMapping => MBResultMapping, SqlCommandType, SqlSource}
import org.apache.ibatis.session.{Configuration => MBConfig}
import org.slf4j.LoggerFactory


private object ConfigurationSpace {

  private var count: Int = 0

  private[config] def generateFQI(spaceId: String, subject: AnyRef) = synchronized {
    count += 1
    FQI(spaceId, subject.getClass.getName.replace('.', '-') + "-" + count)
  }
}

/**
 * Configuration Space (mybatis namespace)
 * @author sunghyouk.bae@gmail.com at 15. 3. 20.
 */
class ConfigurationSpace(configuration: MBConfig, val spaceName: String = "_DEFAULT_") {

  private val log = LoggerFactory.getLogger(getClass)

  private val builderAssistant = new MapperBuilderAssistant(configuration, spaceName)
  builderAssistant.setCurrentNamespace(spaceName)

  def +=(s: Statement): this.type = addStatement(s)

  def ++=(ss: Seq[Statement]): this.type = {
    ss.foreach(s => addStatement(s))
    this
  }

  def ++=(mapper: {def bind: Seq[Statement]}): this.type = ++=(mapper.bind)


  def cache(impl: T[_ <: Cache] = DefaultCache,
            eviction: T[_ <: Cache] = Eviction.LRU,
            flushInterval: Long = -1L,
            size: Int = -1,
            readWrite: Boolean = true,
            props: Properties = null): this.type = {
    builderAssistant.useNewCache(impl.unwrap,
                                  eviction.unwrap,
                                  if (flushInterval > -1) flushInterval else null,
                                  if (size > -1) size else null,
                                  readWrite,
                                  props)
    this
  }

  def cacheRef(that: ConfigurationSpace): this.type = {
    builderAssistant.useCacheRef(that.spaceName)
    this
  }

  private def addResultMap(rm: ResultMap[_]): Unit = {
    if (rm.fqi == null) {
      rm.fqi = ConfigurationSpace.generateFQI(spaceName, rm)
      if (rm.parent != null) addResultMap(rm.parent)
      val resultMappings = new util.ArrayList[MBResultMapping]()

      // Mappings
      (rm.constructor ++ rm.mappings).foreach { r =>
        if (r.nestedSelect != null) addStatement(r.nestedSelect)
        if (r.nestedResultMap != null) addResultMap(r.nestedResultMap)

        resultMappings add builderAssistant.buildResultMapping(r.resultTypeClass,
                                                                r.property,
                                                                r.column,
                                                                r.javaTypeClass,
                                                                r.jdbcTypeEnum,
                                                                resolveFQI(r.nestedSelect),
                                                                resolveFQI(r.nestedResultMap),
                                                                r.notNullColumn,
                                                                r.columnPrefix,
                                                                r.typeHandlerClass,
                                                                r.flags)
      }

      // Discriminator
      var discriminator: Discriminator = _
      rm.discr match {
        case (column, javaType, jdbcType, typeHandler, cases) =>
          val discrimiatorMap = new util.HashMap[String, String]
          cases.foreach { c =>
            addResultMap(c.resultMap)
            discrimiatorMap.put(c.value, c.resultMap.fqi.resolveIn(spaceName))
          }
          discriminator = builderAssistant.buildDiscriminator(rm.resultTypeClass,
                                                               column,
                                                               if (javaType == null || javaType.isVoid) classOf[String] else javaType.unwrap,
                                                               if (jdbcType == null || jdbcType == JdbcType.UNDEFINED) null else jdbcType.unwrap,
                                                               if (typeHandler == null) null else typeHandler.unwrap,
                                                               discrimiatorMap)
        case _ => // Nothing to do.
      }

      // Assemble
      builderAssistant.addResultMap(rm.fqi.id,
                                     rm.resultTypeClass,
                                     if (rm.parent != null) rm.parent.fqi.id else null,
                                     discriminator,
                                     resultMappings,
                                     rm.autoMapping.value)
    }
  }

  private def resolveFQI(r: {def fqi: FQI}): String = {
    if (r == null) null else r.fqi.resolveIn(spaceName)
  }

  private def addStatement(statement: Statement): this.type = {

    if (statement.fqi != null)
      return this

    statement.fqi = ConfigurationSpace.generateFQI(spaceName, statement)
    statement match {
      case stmt: Select => addStatementSelect(stmt)
      case stmt: Insert[_] => addStatementInsert(stmt)
      case stmt: Update[_] => addStatementUpdate(stmt)
      case stmt: Delete[_] => addStatementDelete(stmt)
      case stmt: Perform => addStatementPerform(stmt)
      case unsupported =>
        throw new ConfigurationException(s"Unsupported statement type. statement=$statement")
    }

    this
  }

  private def addStatementSelect(stmt: Select): Unit = {
    if (stmt.resultMap != null) addResultMap(stmt.resultMap)
    builderAssistant.addMappedStatement(stmt.fqi.resolveIn(spaceName),
                                         buildDynamicSQL(stmt.xsql),
                                         stmt.statementType.unwrap,
                                         SqlCommandType.SELECT,
                                         if (stmt.fetchSize > 0) stmt.fetchSize else null,
                                         if (stmt.timeout > -1) stmt.timeout else null,
                                         null,
                                         stmt.parameterTypeClass,
                                         resolveFQI(stmt.resultMap),
                                         stmt.resultTypeClass,
                                         stmt.resultSetType.unwrap,
                                         stmt.flushCache,
                                         stmt.useCache,
                                         false,
                                         new NoKeyGenerator(),
                                         null,
                                         null,
                                         stmt.databaseId,
                                         DefaultScriptingDriver)
  }

  private def addStatementInsert(stmt: Insert[_]): Unit = {
    builderAssistant.addMappedStatement(stmt.fqi.resolveIn(spaceName),
                                         buildDynamicSQL(stmt.xsql),
                                         stmt.statementType.unwrap,
                                         SqlCommandType.INSERT,
                                         null,
                                         if (stmt.timeout > -1) stmt.timeout else null,
                                         null,
                                         stmt.parameterTypeClass,
                                         null,
                                         classOf[Int],
                                         ResultSetType.FORWARD_ONLY.unwrap,
                                         stmt.flushCache,
                                         false,
                                         false,
                                         buildKeyGenerator(stmt.keyGenerator, stmt.parameterTypeClass, stmt.fqi.id, stmt.databaseId),
                                         if (stmt.keyGenerator == null) null else stmt.keyGenerator.keyProeprty,
                                         if (stmt.keyGenerator == null) null else stmt.keyGenerator.keyColumn,
                                         stmt.databaseId,
                                         DefaultScriptingDriver)
  }

  private def addStatementUpdate(stmt: Update[_]): Unit = {
    builderAssistant.addMappedStatement(stmt.fqi.resolveIn(spaceName),
                                         buildDynamicSQL(stmt.xsql),
                                         stmt.statementType.unwrap,
                                         SqlCommandType.UPDATE,
                                         null,
                                         if (stmt.timeout > -1) stmt.timeout else null,
                                         null,
                                         stmt.parameterTypeClass,
                                         null,
                                         classOf[Int],
                                         ResultSetType.FORWARD_ONLY.unwrap,
                                         stmt.flushCache,
                                         false,
                                         false,
                                         new NoKeyGenerator,
                                         null,
                                         null,
                                         stmt.databaseId,
                                         DefaultScriptingDriver)
  }

  private def addStatementDelete(stmt: Delete[_]): Unit = {
    builderAssistant.addMappedStatement(stmt.fqi.resolveIn(spaceName),
                                         buildDynamicSQL(stmt.xsql),
                                         stmt.statementType.unwrap,
                                         SqlCommandType.DELETE,
                                         null,
                                         if (stmt.timeout > -1) stmt.timeout else null,
                                         null,
                                         stmt.parameterTypeClass,
                                         null,
                                         classOf[Int],
                                         ResultSetType.FORWARD_ONLY.unwrap,
                                         stmt.flushCache,
                                         false,
                                         false,
                                         new NoKeyGenerator,
                                         null,
                                         null,
                                         stmt.databaseId,
                                         DefaultScriptingDriver)
  }

  private def addStatementPerform(stmt: Perform): Unit = {
    builderAssistant.addMappedStatement(stmt.fqi.resolveIn(spaceName),
                                         buildDynamicSQL(stmt.xsql),
                                         stmt.statementType.unwrap,
                                         SqlCommandType.UPDATE,
                                         null,
                                         if (stmt.timeout > -1) stmt.timeout else null,
                                         null,
                                         stmt.parameterTypeClass,
                                         null,
                                         classOf[Int],
                                         ResultSetType.FORWARD_ONLY.unwrap,
                                         stmt.flushCache,
                                         false,
                                         false,
                                         new NoKeyGenerator,
                                         null,
                                         null,
                                         stmt.databaseId,
                                         DefaultScriptingDriver)
  }


  private def buildDynamicSQL(xsql: XSQL): SqlSource = new DynamicSQLBuilder(configuration, xsql).build

  private def buildKeyGenerator(generator: KeyGenerator,
                                parameterTypeClass: Class[_],
                                baseId: String,
                                databaseId: String): MBKeyGenerator = {
    generator match {
      case jdbc: JdbcGeneratedKey => new Jdbc3KeyGenerator
      case sql: SqlGeneratedKey[_] => buildSqlKeyGenerator(sql, parameterTypeClass, baseId, databaseId)
      case _ => new NoKeyGenerator
    }
  }

  private def buildSqlKeyGenerator(generator: SqlGeneratedKey[_],
                                   parameterTypeClass: Class[_],
                                   baseId: String,
                                   databaseId: String): MBKeyGenerator = {
    val id = baseId + SelectKeyGenerator.SELECT_KEY_SUFFIX
    builderAssistant.addMappedStatement(id,
                                         buildDynamicSQL(generator.xsql),
                                         generator.statementType.unwrap,
                                         SqlCommandType.SELECT,
                                         null,
                                         null,
                                         null,
                                         parameterTypeClass,
                                         null,
                                         generator.resultTypeClass,
                                         null,
                                         false,
                                         false,
                                         false,
                                         new NoKeyGenerator,
                                         generator.keyProperty,
                                         generator.keyColumn,
                                         databaseId,
                                         DefaultScriptingDriver)

    val keyStmt = configuration.getMappedStatement(id, false)
    val answer = new SelectKeyGenerator(keyStmt, generator.executeBefore)

    configuration.addKeyGenerator(id, answer)
    answer
  }
}
