package debop4s.data.mybatis.session

/**
 * ResultHandlerDelegator
 * @author sunghyouk.bae@gmail.com 15. 3. 19.
 */
class ResultHandlerDelegator(callback: ResultContext => Unit) extends ResultHandler {
  override def handleResult(context: ResultContext): Unit = callback(context)
}
