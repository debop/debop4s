package debop4s.core.jvm

import debop4s.core.conversions.storage._
import debop4s.core.utils.Time


/**
 * NilJvm
 * Created by debop on 2014. 4. 13.
 */
object NilJvm extends Jvm {

  val opts: Opts = new Opts {
    def compileThresh = None
  }

  def forceGc() = System.gc()
  val snapCounters: Map[String, String] = Map()
  val snap: Snapshot = Snapshot(Time.epoch, Heap(0, 0, Seq()), Seq())
  val edenPool = new Pool {def state() = PoolState(0, 0.bytes, 0.bytes) }

}
