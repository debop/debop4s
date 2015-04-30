package debop4s.core

abstract class Func[@miniboxed R] {
  def execute(): R
}

abstract class Func1[@miniboxed -T, @miniboxed R] {
  def execute(arg: T): R
}

abstract class Func2[@miniboxed -T1, @miniboxed -T2, @miniboxed R] {
  def execute(arg1: T1, arg2: T2): R
}

abstract class Func3[@miniboxed -T1, @miniboxed -T2, @miniboxed -T3, @miniboxed R] {
  def execute(arg1: T1, arg2: T2, arg3: T3): R
}

abstract class Func4[@miniboxed -T1, @miniboxed -T2, @miniboxed -T3, @miniboxed -T4, @miniboxed R] {
  def execute(arg1: T1, arg2: T2, arg3: T3, arg4: T4): R
}

abstract class Func5[@miniboxed -T1, @miniboxed -T2, @miniboxed -T3, @miniboxed -T4, @miniboxed -T5, @miniboxed R] {
  def execute(arg1: T1, arg2: T2, arg3: T3, arg4: T4, arg5: T5): R
}
