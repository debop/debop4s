package debop4s.core

/**
 * 인자 없이, void 형을 반환하는 메소드를 가진 인터페이스
 */
abstract class Action {
  def perform()
}

/**
 * 인자 1개를 받고, void 형을 반환하는 메소드를 가진 인터페이스
 */
abstract class Action1[@miniboxed -T] {
  def perform(arg: T)
}

/**
 * 인자 2개를 받고, void 형을 반환하는 메소드를 가진 인터페이스
 */
abstract class Action2[@miniboxed -T1, @miniboxed -T2] {
  def perform(arg1: T1, arg2: T2)
}

/**
 * 인자 3개를 받고, void 형을 반환하는 메소드를 가진 인터페이스
 */
abstract class Action3[@miniboxed -T1, @miniboxed -T2, @miniboxed -T3] {
  def perform(arg1: T1, arg2: T2, arg3: T3)
}

/**
 * 인자 4개를 받고, void 형을 반환하는 메소드를 가진 인터페이스
 */
abstract class Action4[@miniboxed -T1, @miniboxed -T2, @miniboxed -T3, @miniboxed -T4] {
  def perform(arg1: T1, arg2: T2, arg3: T3, arg4: T4)
}

/**
 * 인자 5개를 받고, void 형을 반환하는 메소드를 가진 인터페이스
 */
abstract class Action5[@miniboxed -T1, @miniboxed -T2, @miniboxed -T3, @miniboxed -T4, @miniboxed -T5] {
  def perform(arg1: T1, arg2: T2, arg3: T3, arg4: T4, arg5: T5)
}