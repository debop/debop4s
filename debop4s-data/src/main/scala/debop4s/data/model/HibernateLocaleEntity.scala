package debop4s.data.model

import debop4s.core.ValueObject
import java.util
import java.util.Locale
import scala.collection.JavaConversions._

/**
 * 지역화 정보를 표현하는 trait 입니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 26. 오전 9:42
 */
trait LocaleValue extends ValueObject {}


/**
 * 지역화 정보를 가지는 엔티티의 기본 클래스입니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2014. 2. 26. 오전 9:41
 */
@SerialVersionUID(-2891849618560053560L)
trait HibernateLocaleEntity[TLocaleVal <: LocaleValue] extends PersistentObject {

  protected val localeMap: util.Map[Locale, TLocaleVal] = new util.HashMap[Locale, TLocaleVal]()

  protected def createDefaultLocaleVal: TLocaleVal

  def getDefaultLocale: TLocaleVal = {
    createDefaultLocaleVal
  }

  /**
   * 특정 지역에 해당하는 정보
   *
   * @param locale Locale 정보
   * @return 특정 지역에 해당하는 정보
   */
  def getLocaleValue(locale: Locale): TLocaleVal =
    getLocaleValueOrDefault(locale)

  /**
   * 특정 지역의 정보를 가져옵니다. 만약 해당 지역의 정보가 없다면 엔티티의 정보를 이용한 정보를 제공합니다.
   *
   * @param locale 지역 정보
   * @return 지역화 정보
   */
  def getLocaleValueOrDefault(locale: Locale = Locale.getDefault): TLocaleVal = {
    if (localeMap == null || localeMap.size == 0)
      return getDefaultLocale

    if (locale == null || locale.getDisplayName == null)
      return getDefaultLocale

    localeMap.getOrElse(locale, getDefaultLocale)
  }

  /**
   * 현 Thread Context 에 해당하는 지역의 정보를 제공합니다.
   *
   * @return 지역화 정보
   */
  def getCurrentLocaleValue: TLocaleVal = {
    getLocaleValueOrDefault(Locale.getDefault)
  }

  /**
   * 엔티티가 보유한 지역 정보
   *
   * @return
   */
  def getLocales: Set[Locale] = localeMap.keySet.toSet

  /**
   * 엔티티에 지역화 정보를 추가합니다.
   *
   * @param locale      지역 정보
   * @param localeValue 해당 지역에 해당하는 정보
   */
  def addLocaleValue(locale: Locale, localeValue: TLocaleVal) {
    localeMap.update(locale, localeValue)
  }

  /**
   * 특정 지역의 정보를 제거합니다.
   *
   * @param locale 지역 정보
   */
  def removeLocaleValue(locale: Locale) {
    localeMap.remove(locale)
  }

}
