package com.github.debop4s.core

/**
 * com.github.debop4s.core.NotSupportedException
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 27. 오전 11:53
 */
class NotSupportedException(msg: String, cause: Throwable)
    extends RuntimeException(msg, cause) {

    def this() {
        this(null, null)
    }

    def this(msg: String) {
        this(msg, null)
    }

    def this(cause: Throwable) {
        this(null, cause)
    }
}

