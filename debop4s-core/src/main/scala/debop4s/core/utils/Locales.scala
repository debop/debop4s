package debop4s.core.utils

import java.util
import java.util.Locale

import debop4s.core.Logging

/**
 * 로케일 (`Locale`) 정보를 쉽게 사용하기 위한 Utility class
 * @author sunghyouk.bae@gmail.com
 */
object Locales extends Logging {

  object Implicits {

    implicit class LocaleExtensions(underlying: Locale) {

      /** 기본 Locale인가? */
      def isDefault: Boolean = Locale.getDefault.equals(underlying)

      /** Locale이 null 이라면, 시스템 기본 Locale을 반환한다 */
      def orDefault: Locale = if (underlying != null) underlying else Locale.getDefault

      /** 상위 Locale 을 반환한다 */
      def parent: Locale = {
        log.debug(s"underlying 의 부모 Locale을 구합니다. underlying=$underlying")
        val variant = underlying.getVariant
        val country = underlying.getCountry
        val language = underlying.getLanguage

        if (variant.length > 0 && (language.length > 0 || country.length > 0)) {
          return new Locale(language, country)
        }
        if (country.length > 0)
          return new Locale(language)

        null
      }

      /** 조상 Locale 들을 모두 구합니다 */
      def parents: util.List[Locale] = {
        log.debug(s"underlying locale의 조상 locale 들을 구합니다. underlying=$underlying")

        val results = new util.ArrayList[Locale]()
        var currLoc = underlying
        while (currLoc != null) {
          results.add(0, currLoc)
          currLoc = currLoc.parent
        }

        log.debug(s"조상 locales = $results")
        results
      }
    }
  }

  /**
   * Calculate the filenames for the given bundle basename and Locale,
   * appending language code, country code, and variant code.
   * E.g.: basename "messages", Locale "de_AT_oo" -> "messages_de_AT_OO",
   * "messages_de_AT", "messages_de".
   * <p>Follows the rules defined by {@link java.util.Locale#toString()}.
   * @param basename the basename of the bundle
   * @param locale the locale
   * @return the List of filenames to check
   */
  def calculateFilenamesForLocale(basename: String, locale: Locale): util.List[String] = {
    log.debug(s"Locale에 해당하는 파일명을 조합합니다. basename=$basename, locale=$locale")
    val result = new util.ArrayList[String](3)
    val language = locale.getLanguage
    val country = locale.getCountry
    val variant = locale.getVariant

    val temp: StringBuilder = new StringBuilder(basename)
    temp.append('_')

    if (language.length > 0) {
      temp.append(language)
      result.add(0, temp.toString())
    }

    temp.append('_')
    if (country.length > 0) {
      temp.append(country)
      result.add(0, temp.toString())
    }

    if (variant.length > 0 && (language.length > 0 || country.length > 0)) {
      temp.append('_').append(variant)
      result.add(0, temp.toString())
    }
    log.debug(s"Locale에 해당하는 파일명을 조합했습니다. result=$result")
    result
  }
}
