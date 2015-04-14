package debop4s.core

abstract class Func[R] {
  def execute(): R
}

abstract class Func1[-T, R] {
  def execute(arg: T): R
}

abstract class Func2[-T1, -T2, R] {
  def execute(arg1: T1, arg2: T2): R
}

abstract class Func3[-T1, -T2, -T3, R] {
  def execute(arg1: T1, arg2: T2, arg3: T3): R
}

abstract class Func4[-T1, -T2, -T3, -T4, R] {
  def execute(arg1: T1, arg2: T2, arg3: T3, arg4: T4): R
}

abstract class Func5[-T1, -T2, -T3, -T4, -T5, R] {
  def execute(arg1: T1, arg2: T2, arg3: T3, arg4: T4, arg5: T5): R
}
