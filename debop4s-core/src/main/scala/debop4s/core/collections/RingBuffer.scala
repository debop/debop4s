package debop4s.core.collections

import scala.reflect.ClassTag

/**
 * 제한된 요소 수를 가지는 고리 모양의 버퍼를 표현합니다.
 * @author Sunghyouk Bae
 */
class RingBuffer[A: ClassTag](val maxSize: Int) extends Seq[A] {

    private val array = new Array[A](maxSize)
    private var read = 0
    private var write = 0
    private var _count = 0

    def length = _count
    override def size = _count

    def clear() {
        read = 0
        write = 0
        _count = 0
    }

    /**
     * Gets the element from the specified index in constant time.
     */
    def apply(i: Int): A = {
        if (i >= _count) throw new IndexOutOfBoundsException(i.toString)
        else array((read + i) % maxSize)
    }

    /**
     * Overwrites an element with a new value
     */
    def update(i: Int, elem: A) {
        if (i >= _count) throw new IndexOutOfBoundsException(i.toString)
        else array((read + i) % maxSize) = elem
    }

    /**
     * 새로운 요소를 추가합니다.
     */
    def +=(elem: A) {
        array(write) = elem
        write = (write + 1) % maxSize
        if (_count == maxSize) read = (read + 1) % maxSize
        else _count += 1
    }

    def ++=(iter: Iterable[A]) {
        for (elem <- iter) this += elem
    }

    /**
     * 버퍼로부터 다음 요소를 제거하고 반환합니다.
     */
    def next: A = {
        if (read == write) throw new NoSuchElementException
        else {
            val res = array(read)
            read = (read + 1) % maxSize
            _count -= 1
            res
        }
    }

    override def iterator = new Iterator[A] {
        var idx = 0
        def hasNext = idx != _count
        def next() = {
            val res = apply(idx)
            idx += 1
            res
        }
    }
    /**
     * 지정된 갯수만큼 버퍼의 요소를 삭제합니다.
     */
    override def drop(n: Int): RingBuffer[A] = {
        if (n >= maxSize) clear()
        else read = (read + n) % maxSize
        this
    }

    def removeWhere(predicate: A => Boolean): Int = {
        var rmCount = 0: Int
        var j = 0
        for (i <- 0 until _count) {
            val elem = apply(i)
            if (predicate(elem)) rmCount += 1
            else {
                if (j < i) update(j, elem)
                j += 1
            }
        }
        _count -= rmCount
        write = (read + _count) % maxSize
        rmCount
    }
}

object RingBuffer {
    def apply[A: ClassTag](maxSize: Int): RingBuffer[A] =
        new RingBuffer(1 max maxSize)
}
