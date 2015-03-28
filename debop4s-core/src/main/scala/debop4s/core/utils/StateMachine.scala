package debop4s.core.utils

/**
 * StateMachine
 * @author Sunghyouk Bae
 */
object StateMachine {

  class InvalidStateTransition(fromState: String, command: String)
    extends Exception(s"Transitioning from [$fromState] via commmand [$command]")

}

trait StateMachine {

  import StateMachine._

  protected trait State
  protected var state: State = _

  def getState = this.state

  /**
   * 현재 상태가 case 구문에 존재하면 처리하고, 없으면 `InvalidStateTransition` 예외를 발생시킵니다.
   */
  protected def transition[A](command: String)(f: PartialFunction[State, A]) = synchronized {
    if (f.isDefinedAt(state)) {
      f(state)
    } else {
      throw new InvalidStateTransition(state.toString, command)
    }
  }
}
