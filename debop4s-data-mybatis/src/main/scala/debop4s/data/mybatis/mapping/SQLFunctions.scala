package debop4s.data.mybatis.mapping

import debop4s.data.mybatis.session.Session

trait SQLFunction0[+R] {
  def apply()(implicit s: Session): R
}

trait SQLFunction1[-A, +R] {
  def apply(a: A)(implicit s: Session): R
}

trait SQLFunction2[-A, -B, +R] {
  def apply(a: A, b: B)(implicit s: Session): R
}