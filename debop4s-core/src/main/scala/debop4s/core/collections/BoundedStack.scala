package debop4s.core.collections

import scala.reflect.ClassTag

/**
 * [[BoundedStack]] 의 companion object
 */
object BoundedStack {
  def apply[A: ClassTag](maxSize: Option[Int] = None): BoundedStack[A] =
    new BoundedStack[A](maxSize.getOrElse(16))
}
/**
 * 제한된 크기를 가지는 `stack` 입니다. 제한된 크기보다 더 많은 요소를 추가 시, 가장 아래에 있는 요소를 제거합니다.
 */
class BoundedStack[@miniboxed A: ClassTag](val maxSize: Int) extends Seq[A] {

  require(maxSize > 0, s"maxSize should greater than 0")

  private val array = new Array[A](maxSize)
  private var top: Int = 0
  private var _count: Int = 0

  def length: Int = _count
  override def size: Int = _count

  def clear(): Unit = {
    top = 0
    _count = 0
  }

  /** 특정 순서의 요소를 조회합니다 */
  def apply(index: Int): A = {
    if (index >= _count) throw new IndexOutOfBoundsException(index.toString)
    else array((top + index) % maxSize)
  }

  def +=(elem: A): Unit = {
    top = if (top == 0) maxSize - 1 else top - 1
    array(top) = elem
    if (_count < maxSize) _count += 1
  }

  def insert(index: Int, elem: A): Unit = {
    if (index == 0) this += elem
    else if (index > _count) throw new IndexOutOfBoundsException(index.toString)
    else if (index == _count) {
      array((top + index) % maxSize) = elem
      _count += 1
    } else {
      val swapped = this(index)
      this(index) = elem
      insert(index - 1, swapped)
    }
  }

  /**
   * 해당 인덱스의 요소를 `elem` 으로 변경한다.
   */
  def update(index: Int, elem: A): Unit = {
    array((top + index) % maxSize) = elem
  }

  def ++=(iter: Iterable[A]): Unit = {
    val it = iter.iterator
    while (it.hasNext) {
      this += it.next()
    }
  }

  /** 최상위 요소를 꺼냅니다. */
  def pop: A = {
    if (_count == 0) throw new NoSuchElementException
    else {
      val res = array(top)
      top = (top + 1) % maxSize
      _count -= 1
      res
    }
  }

  /** 요소를 추가합니다. */
  def push(elem: A): Unit = {
    this += elem
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
}
