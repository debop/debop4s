package debop4s.data.model

import debop4s.core.ValueObject
import debop4s.core.utils.{Hashs, ToStringHelper}
import javax.persistence.{Embeddable, Column}

/**
 * TREE 상에서 NODE의 위치를 나타냅니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 24. 오후 4:03
 */
@SerialVersionUID(3455568346636164669L)
@Embeddable
case class TreeNodePosition(var lvl: Int, var ord: Int) extends ValueObject {

  def this() {
    this(0, 0)
  }

  @Column(name = "nodeLevel")
  def getLvl: Int = lvl

  @Column(name = "nodeOrder")
  def getOrd: Int = ord

  def setPosition(level: Int, order: Int) {
    this.lvl = level
    this.ord = order
  }

  override def hashCode(): Int = Hashs.compute(lvl, ord)

  override protected def buildStringHelper: ToStringHelper =
    super.buildStringHelper
      .add("lvl", lvl)
      .add("ord", ord)
}
