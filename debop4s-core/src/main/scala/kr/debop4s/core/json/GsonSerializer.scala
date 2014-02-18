package kr.debop4s.core.json

import com.google.gson.Gson
import org.slf4j.LoggerFactory

/**
 * kr.debop4s.core.json.GsonSerializer
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 11. 오후 1:03
 */
class GsonSerializer(val gson: Gson) extends JsonSerializer {

    require(gson != null)

    lazy val log = LoggerFactory.getLogger(getClass)

    def this() {
        this(new Gson())
    }

    override def serializeToText(graph: AnyRef): String = gson.toJson(graph)

    override def deserializeFromText[T <: AnyRef](text: String, clazz: Class[T]): T =
        gson.fromJson[T](text, clazz)
}

