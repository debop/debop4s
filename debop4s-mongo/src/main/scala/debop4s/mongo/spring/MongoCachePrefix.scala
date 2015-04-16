package debop4s.mongo.spring

trait MongoCachePrefix {
  def prefix(cacheName: String): String
}

object MongoCachePrefix {

  val DEFAULT_DELIMETER = ":"

  def apply(): MongoCachePrefix = new DefaultMongoCachePrefix()

  def apply(delimeter: Option[String]) = new DefaultMongoCachePrefix(delimeter)
}

class DefaultMongoCachePrefix(val delimeter: Option[String] = Some(MongoCachePrefix.DEFAULT_DELIMETER))
  extends MongoCachePrefix {

  override def prefix(cacheName: String): String = {
    delimeter.fold(cacheName) { d => cacheName.concat(d) }
  }
}
