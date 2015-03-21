package debop4s.config.base

import com.typesafe.config.Config

/**
 * Typesafe config 라이브러리의 환경설정 정보의 가장 기본 단위를 표현하는 trait 입니다.
 * @author sunghyouk.bae@gmail.com 15. 3. 21.
 */
trait ConfigElementSupport {

  /**
   * 현 Element 가 속한 `Config` 인스턴스를 나타냅니다.
   * @return
   */
  protected def config: Config

  /**
   * 설정 Key에 해당하는 값을 조회합니다.
   * @param key 설정 키
   * @return 설정 값
   */
  def getString(key: String): String = config.getString(key)

  /**
   * 현 위치의 `Config` 로부터 상대 경로에 있는 `Config` 노드를 반환합니다.
   * 자손 config를 찾을 수 있습니다.
   * @param path 현 위치를 기준으로 하는 상대적인 경로
   * @return 찾고자하는 `Config`
   */
  def getConfig(path: String): Config = config.getConfig(path)

}
