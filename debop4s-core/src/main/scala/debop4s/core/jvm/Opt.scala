package debop4s.core.jvm

import java.lang.management.ManagementFactory
import javax.management.openmbean.CompositeDataSupport
import javax.management.{RuntimeMBeanException, ObjectName}

/**
 * Retrieve the named JVM option.
 * Created by debop on 2014. 4. 14.
 */
object Opt {

    private[this] val DiagnosticName = ObjectName.getInstance("com.sun.management:type=HotSpotDiagnostic")

    def apply(name: String): Option[String] = {
        try {
            val o = ManagementFactory.getPlatformMBeanServer.invoke(
                DiagnosticName,
                "getVMOption",
                Array(name),
                Array("java.lang.String")
            )
            Some(o.asInstanceOf[CompositeDataSupport].get("value").asInstanceOf[String])
        } catch {
            case _: IllegalArgumentException => None
            case rbe: RuntimeMBeanException if rbe.getCause.isInstanceOf[IllegalArgumentException] => None
        }
    }

}
