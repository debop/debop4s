import java.io.File

import org.apache.catalina.startup.Tomcat

object TomcatLauncher extends App {

  val tomcat = new Tomcat()

  val webAppDir = "src/test/webapp"

  var port = System.getenv("PORT")
  if (port == null || port.isEmpty) port = "8080"

  tomcat.setPort(port.toInt)

  tomcat.addWebapp("/", new File(webAppDir).getAbsolutePath)

  tomcat.start()
  tomcat.getServer.await()

}
