package debop4s.core

/**
 * Java 에서 Object 를 Mixin 으로 사용하기 위한 trait 입니다.
 * Created by debop on 2014. 4. 5.
 */
trait JavaSingleton {
  def get = this
}
