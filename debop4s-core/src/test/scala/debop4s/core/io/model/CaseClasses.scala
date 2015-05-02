package debop4s.core.io.model

import debop4s.core.utils.Hashs

trait Entity[@miniboxed T] {

  def id: T

  override def equals(obj: Any): Boolean = {
    obj != null &&
    getClass == obj.getClass &&
    this.hashCode == obj.hashCode()
  }
}
case class PersonEntity(override val id: Long,
                        name: String,
                        kidsAges: Array[Int]) extends Entity[Long] {
  override def hashCode(): Int = Hashs.compute(id, name)
}

case class CompanyEntity(override val id: Long,
                         name: String,
                         code: String) extends Entity[Long] {
  override def hashCode(): Int = Hashs.compute(id, name)
}

