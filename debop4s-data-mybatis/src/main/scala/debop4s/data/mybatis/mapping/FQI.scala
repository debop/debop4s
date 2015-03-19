package debop4s.data.mybatis.mapping

/**
 * Fully Qualified Identifier
 * @author sunghyouk.bae@gmail.com 15. 3. 19.
 */
private[mybatis] case class FQI(spaceId: String, localId: String) {

  def resolveIn(externalSpaceId: String): String = {
    if (externalSpaceId == spaceId) localId
    else id
  }

  val id = spaceId + "." + localId
}
