package com.github.debop4s.core.logback

import com.github.debop4s.core.AbstractValueObject
import org.joda.time.DateTime
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

/**
 * logback 로그 정보를 표현하는 클래스입니다.
 * Created by debop on 2014. 2. 22.
 */
@SerialVersionUID(1431014486199195378L)
class LogDocument extends AbstractValueObject {

    var serverName: String = null

    var applicationName: String = null

    var logger: String = null

    var levelInt: Int = 0

    var levelStr: String = null

    var threadName: String = null

    var message: String = null

    var timestamp: DateTime = null

    var marker: String = null

    var exception: String = null

    var stacktrace: mutable.Buffer[String] = ArrayBuffer[String]()
}
