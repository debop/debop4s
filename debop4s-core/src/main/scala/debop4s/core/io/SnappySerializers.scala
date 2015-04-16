package debop4s.core.io

import debop4s.core.compress.SnappyCompressor


object SnappyBinarySerializer {
  def apply(): SnappyBinarySerializer = new SnappyBinarySerializer()
}

class SnappyBinarySerializer extends CompressableSerializer(BinarySerializer(), SnappyCompressor())

object SnappyFstSerializer {
  def apply(): SnappyFstSerializer = new SnappyFstSerializer()
}

class SnappyFstSerializer extends CompressableSerializer(FstSerializer(), SnappyCompressor())

