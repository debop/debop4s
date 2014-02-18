package kr.debop4s.core.utils

import scala.collection.mutable

/**
 * kr.debop4s.core.tools.ToStringHelper
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 10. 오후 1:31
 */
class ToStringHelper(val className: String) {

    val map = new mutable.LinkedHashMap[String, Any]()

    def add(name: String, value: Any): ToStringHelper = {
        addMap(name, value)
        this
    }

    @inline
    override def toString: String = {
        val builder = new StringBuilder()

        builder.append("{")
        var sep = ""
        var first = true
        for (x <- map) {
            builder.append(sep)
            builder.append(x._1).append("=").append(x._2)
            if (first) {
                first = false
                sep = ","
            }
        }
        builder.append("}")
        builder.toString()
    }

    private def addMap(name: String, value: Any) {
        map.put(name, value)
    }
}

object ToStringHelper {
    def apply(self: Any) = new ToStringHelper(self.getClass.getName)
}
