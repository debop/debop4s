package debop4s.data.mybatis.mapping

/**
  * Utility to wrap types with reflection capabilities via Manifest.
  * Use T[MyType] instead of classOf[MyType]
  * @tparam t wrapped type
  */
class T[t: Manifest] {

  val raw = manifest[t].runtimeClass.asInstanceOf[Class[Any]]
  val unwrap = manifest[t].runtimeClass.asInstanceOf[Class[t]]
  val isVoid = unwrap == java.lang.Void.TYPE

}

/** Syntactic sugar to support "T[MyType]" instead of new T[MyType] */
object T {
  def apply[t: Manifest] = new T[t]
}
