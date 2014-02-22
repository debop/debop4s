package com.github.debop4s.core.utils

/**
 * com.github.debop4s.core.utils.Options
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2014. 1. 5. 오후 11:50
 */
object Options {

    def get[T](v: T): Option[T] = if (v == null) None else Some(v)

}
