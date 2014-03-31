package com.github.debop4s.core.utils

import org.jetbrains.annotations.Nullable

/**
 * Closer
 * @author Sunghyouk Bae
 */
object Closer {

    /**
     * `close` 메소드를 가진 객체에 대해 메소드 `func` 를 실행한 후 `close` 메소드를 호출합니다.
     */
    def using[A <: {def close() : Unit}, B](@Nullable closable: A)(func: A => B): B = {
        require(closable != null)
        require(func != null)
        try {
            func(closable)
        } finally {
            closable.close()
        }
    }

}
