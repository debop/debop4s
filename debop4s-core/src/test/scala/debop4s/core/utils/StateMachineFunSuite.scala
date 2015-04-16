package debop4s.core.utils

import debop4s.core.AbstractCoreFunSuite

/**
 * StateMachineFunSuite
 * @author Sunghyouk Bae
 */
class StateMachineFunSuite extends AbstractCoreFunSuite {

  private def createStateMachine() =
    new StateMachine {
      object Status1 extends State
      object Status2 extends State

      state = Status1

      def command1() {
        transition("command1") {
          case Status1 =>
            // "ok"
            state = Status2
        }
      }
    }

  test("allow transitions that are permitted") {
    val stateMachine = createStateMachine()
    stateMachine.command1()
  }

  test("throws exceptions when a transition is not permitted") {
    val stateMachine = createStateMachine()
    stateMachine.command1()
    intercept[StateMachine.InvalidStateTransition] {
      stateMachine.command1()
    }
  }
}
