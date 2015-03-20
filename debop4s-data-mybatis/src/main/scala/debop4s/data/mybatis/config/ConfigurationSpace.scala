package debop4s.data.mybatis.config

import java.util
import java.util.Properties

import debop4s.data.mybatis.cache._
import debop4s.data.mybatis.mapping._
import org.apache.ibatis.builder.MapperBuilderAssistant
import org.apache.ibatis.cache.Cache
import org.apache.ibatis.mapping.{ResultMapping => MBResultMapping}
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
    if(rm.fqi==null) {
      rm.fqi = ConfigurationSpace.generateFQI(spaceName, rm)
      if(rm.parent != null) addResultMap(rm.parent)
      val resultMappings = new util.ArrayList[MBResultMapping]()

      // Mappings
      (rm.constructor ++ rm.mappings).foreach { r =>
        if(r.nestedSelect != null) addStatement(r.nestedSelect)
        if(r.nestedResultMap != null) addResultMap(r.nestedResultMap)
      }
    }
  }

  private def addStatement(s: Statement): this.type = {

    this
  }


}
