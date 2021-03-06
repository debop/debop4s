package debop4s.core

abstract class Function0[@miniboxed R] extends (() => R)

abstract class ExceptionalFunction0[R] extends Function0[R] {

  /** Implements apply in terms of abstract applyE, to allow Java code to throw checked exceptions. */
  final override def apply(): R = applyE()

  @throws(classOf[Throwable])
  def applyE(): R
}

abstract class Function[@miniboxed -T1, @miniboxed +R] extends PartialFunction[T1, R] {

  /** These overrides do nothing but delegate to super. They are necessary for Java compatibility. */
  override def compose[A](g: A => T1): A => R = super.compose(g)

  /** These overrides do nothing but delegate to super. They are necessary for Java compatibility. */
  override def andThen[A](g: R => A): PartialFunction[T1, A] = super.andThen(g)
}

object Function {

  def synchronizeWith[T, R](m: Object)(f: T => R): T => R =
    t => m.synchronized { f(t) }
}

abstract class ExceptionalFunction[@miniboxed -T1, @miniboxed R] extends Function[T1, R] {

  final override def apply(in: T1): R = applyE(in)

  @throws(classOf[Throwable])
  def applyE(in: T1): R
}

abstract class Function2[@miniboxed -T1, @miniboxed -T2, @miniboxed R] extends ((T1, T2) => R)

abstract class Command[@miniboxed -T1] extends (T1 => Unit) {
  override def andThen[A](g: (Unit) => A): (T1) => A = super.andThen(g)
  override def compose[A](g: (A) => T1): (A) => Unit = super.compose(g)
}