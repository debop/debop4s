package debop4s.data.mybatis.config

import org.apache.ibatis.mapping.SqlSource
import org.apache.ibatis.scripting.xmltags._
import org.apache.ibatis.session.{ Configuration => MBConfig }
import org.slf4j.LoggerFactory

import scala.collection.JavaConverters._
import scala.collection.mutable.ArrayBuffer
import scala.xml._

/**
 * DynamicSQLBuilder
 * @author sunghyouk.bae@gmail.com at 15. 3. 20.
 */
private[mybatis] class DynamicSQLBuilder(val configuration: MBConfig, val node: Node) {

  private val LOG = LoggerFactory.getLogger(getClass)

  def build: SqlSource = new DynamicSqlSource(configuration, parse(node))

  @inline
  private def parse(n: Node): SqlNode = {

    LOG.trace(s"parse node ... node=${ n.toString() }")

    n match {
      case Text(text) => new TextSqlNode(text)
      case PCData(text) => new TextSqlNode(text)

      case <xsql>{children @ _*}</xsql> =>
        parseChildren(children)

      case trim @ <trim>{children @ _*}</trim> =>
        val contents = parseChildren(children)
        new TrimSqlNode(configuration,
                         contents,
                         attr(trim, "@prefix"),
                         attr(trim, "@prefixOverrides"),
                         attr(trim, "@suffix"),
                         attr(trim, "@suffixOverrides"))

      case <where>{children @ _*}</where> =>
        val contents = parseChildren(children)
        new WhereSqlNode(configuration, contents)

      case <set>{children @ _*}</set> =>
        val contents = parseChildren(children)
        new SetSqlNode(configuration, contents)

      case foreach @ <foreach>{children @ _*}</foreach> =>
        val contents = parseChildren(children)
        new ForEachSqlNode(configuration,
                            contents,
                            attr(foreach, "@collection"),
                            attr(foreach, "@index"),
                            attr(foreach, "@item"),
                            attr(foreach, "@open"),
                            attr(foreach, "@close"),
                            attr(foreach, "@separator"))

      case ifNode @ <if>{children @ _*}</if> =>
        val contents = parseChildren(children)
        new IfSqlNode(contents, attr(ifNode, "@test"))

      case <choose>{children @ _*}</choose> =>
        val ifNodes = ArrayBuffer[SqlNode]()
        var defaultNode: MixedSqlNode = null

        children.foreach {
          case when @ <when>{ch @ _*}</when> =>
            val contents = parseChildren(ch)
            ifNodes += new IfSqlNode(contents, attr(when, "@test"))

          case other @ <otherwise>{ch @ _*}</otherwise> =>
            if (defaultNode == null) defaultNode = parseChildren(ch)
            else throw new ConfigurationException(s"Too many default (otherwise) elements in choose statement.")

          case _ =>
        }
        new ChooseSqlNode(ifNodes.asJava, defaultNode)

      case ifNode @ <when>{children @ _*}</when> =>
        val contents = parseChildren(children)
        new IfSqlNode(contents, attr(ifNode, "@test"))

      case other @ <otherwise>{children @ _*}</otherwise> =>
        parseChildren(other)

      case a: Atom[String] =>
        new TextSqlNode(a.data)

      case bind @ <bind/> =>
        new VarDeclSqlNode(attr(bind, "@name"), attr(bind, "@value"))

      case unsupported =>
        throw new ConfigurationException(s"Unknown element ${ unsupported.getClass.getName } in SQL statement.")
    }
  }

  @inline
  private def parseChildren(children: Seq[Node]): MixedSqlNode = {
    val nodes = children.map(child => parse(child))
    new MixedSqlNode(nodes.asJava)
  }

  @inline
  private def attr(n: Node, name: String): String = {
    ( n \ name ).text match {
      case "" => null
      case text => text
    }
  }

}
