package com.github.debop4s.core.parallels

/**
 * 타임아웃이 있는 실행 객체의 trait
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 10. 오후 2:05
 */
trait Executable {

  /** 특정 코드를 수행합니다. */
  def execute()

  /** 설정한 타임아웃이 되었을 때 호출되는 메소드입니다. */
  def timeout()
}
