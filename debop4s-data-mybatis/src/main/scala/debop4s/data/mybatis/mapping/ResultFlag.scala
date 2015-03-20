package debop4s.data.mybatis.mapping

import org.apache.ibatis.mapping.{ResultFlag => MBResultFlag}

private [mybatis] sealed trait ResultFlag {

  val unwrap: MBResultFlag

}

private [mybatis] object ResultFlag {
  val ID = new ResultFlag { override val unwrap = MBResultFlag.ID }
  val CONSTRUCTOR = new ResultFlag { override val unwrap = MBResultFlag.CONSTRUCTOR }
}
