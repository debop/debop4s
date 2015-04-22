package debop4s.core.jvm

import java.lang.Thread.State._
import java.lang.management.{ManagementFactory, ThreadInfo, ThreadMXBean}

/**
 * A thread contention summary providing a brief overview of threads
 * that are [[http://docs.oracle.com/javase/1.5.0/docs/api/java/lang/Thread.State.html#BLOCKED BLOCKED]], [[http://docs.oracle.com/javase/1.5.0/docs/api/java/lang/Thread.State.html#WAITING WAITING]], or [[http://docs.oracle.com/javase/1.5.0/docs/api/java/lang/Thread.State.html#TIMED_WAITING TIMED_WAITING]]
 *
 * While this could be an object, we use instantiation as a signal of
 * intent and enable contention monitoring.
 */
class ContentionSnapshot {

  ManagementFactory.getThreadMXBean.setThreadContentionMonitoringEnabled(true)

  case class Snapshot(blockedThreads: Seq[String],
                      lockOwners: Seq[String],
                      deadlocks: Seq[String])

  private[this] object Blocked {
    def unapply(t: ThreadInfo): Option[ThreadInfo] = {
      t.getThreadState match {
        case BLOCKED | WAITING | TIMED_WAITING => Some(t)
        case _ => None
      }
    }
  }

  def snap(): Snapshot = {
    val bean: ThreadMXBean = ManagementFactory.getThreadMXBean

    val blocked: Array[ThreadInfo] = bean.getThreadInfo(bean.getAllThreadIds, true, true)
                                     .filter(_ != null)
                                     .collect { case Blocked(info) => info }

    val ownerIds: Array[Long] = blocked map (_.getLockOwnerId) filter (_ != -1)
    val owners: Seq[String] =
      if (ownerIds.length == 0) Seq[String]()
      else bean.getThreadInfo(ownerIds.toArray, true, true).map(_.toString).toSeq

    val deadlockThreadIds: Array[Long] = bean.findDeadlockedThreads()

    val deadlocks: Array[ThreadInfo] =
      if (deadlockThreadIds == null) Array.empty[ThreadInfo]
      else deadlockThreadIds.flatMap { id =>
        blocked.find { threadInfo => threadInfo.getThreadId == id }
      }

    Snapshot(blockedThreads = blocked.map(_.toString).toSeq,
             lockOwners = owners,
             deadlocks = deadlocks.map(_.toString).toSeq)
  }
}

