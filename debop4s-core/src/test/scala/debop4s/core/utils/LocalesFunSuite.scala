package debop4s.core.utils

import java.util.Locale

import debop4s.core.AbstractCoreFunSuite
import debop4s.core.utils.Locales.Implicits._

import scala.collection.JavaConverters._

/**
 * LocalesFunSuite
 * @author sunghyouk.bae@gmail.com
 */
class LocalesFunSuite extends AbstractCoreFunSuite {

  test("시스템 기본 Locale 인가?") {
    Locale.KOREA.isDefault shouldEqual true
  }

  test("orDefault") {
    val nullLocale = null: Locale
    nullLocale.orDefault shouldEqual Locale.getDefault
  }

  test("부모 Locale 구하기") {
    Locale.KOREA.parent shouldEqual Locale.KOREAN
    Locale.KOREAN.parent shouldEqual null
    Locale.US.parent shouldEqual Locale.ENGLISH
  }

  test("조상 Locale 구하기") {
    val parents = Locale.KOREA.parents

    parents.asScala.foreach { locale =>
      log.debug(s"Locale=$locale")
    }
  }

}
