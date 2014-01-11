package kr.debop4s.core.json

import com.google.gson.Gson
import kr.debop4s.core.logging.Logger

/**
 * kr.debop4s.core.json.GsonSerializer
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 11. 오후 1:03
 */
class GsonSerializer(val gson: Gson) extends JsonSerializer {

    implicit lazy val log = Logger[GsonSerializer]

    def this() {
        this(new Gson())
    }

    override def serializeToText(graph: AnyRef): String = gson.toJson(graph)

    override def deserializeFromText[T <: AnyRef](text: String, clazz: Class[T]): T =
        gson.fromJson[T](text, clazz)
}

