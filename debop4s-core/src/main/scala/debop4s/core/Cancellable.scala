package debop4s.core

import java.util.concurrent.atomic.AtomicBoolean

/**
 * 취소가 가능한 객체를 표현합니다.
 * Created by debop on 2014. 4. 13.
 */
trait Cancellable {
    /**
    * 취소 여부를 나타냅니다.
    */
    def isCancelled: Boolean
    /**
     * 작업을 취소합니다. 취소 작업은 연결된 `cancellable object` 에게 전파된다.
     */
    def cancel()

    /**
     * 현 `cancellable`에 `other`를 연결합니다.
     * 이렇게 되면 현 `cancellable`이 취소되었을 때에 `other` 객체도 취소됩니다.
     */
    def linkTo(other: Cancellable): Unit
}

object Cancellable {
    val Nil: Cancellable = new Cancellable {
        def isCancelled = false
        def cancel() {}
        def linkTo(other: Cancellable) {}
    }
}

class CancellableSink(cancelTask: => Unit) extends Cancellable {

    private[this] val wasCancelled = new AtomicBoolean(false)
    override def isCancelled: Boolean = wasCancelled.get()
    override def cancel(): Unit = {
        if (wasCancelled.compareAndSet(false, true)) {
            cancelTask
        }
    }
    /**
     * 현 `cancellable`에 `other`를 연결합니다.
     * 이렇게 되면 현 `cancellable`이 취소되었을 때에 `other` 객체도 취소됩니다.
     */
    override def linkTo(other: Cancellable): Unit = {
        throw new NotSupportedException("linking not supported in CancellableSink")
    }
}
