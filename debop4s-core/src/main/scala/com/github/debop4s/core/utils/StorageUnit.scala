package com.github.debop4s.core.utils

import com.github.debop4s.core.ValueObject
import scala.annotation.switch


/**
 * 저장 용량에 대한 단위를 표현합니다.
 * Created by debop on 2014. 4. 5.
 */
class StorageUnit(val bytes: Long) extends Ordered[StorageUnit] {

  def inBytes = bytes
  def inKiloBytes = bytes / 1024L
  def inMegaBytes = inKiloBytes / 1024L
  def inGigaBytes = inMegaBytes / 1024L
  def inTeraBytes = inGigaBytes / 1024L
  def inPetaBytes = inTeraBytes / 1024L
  def inExaBytes = inPetaBytes / 1024L

  def +(that: StorageUnit): StorageUnit = new StorageUnit(this.bytes + that.bytes)
  def -(that: StorageUnit): StorageUnit = new StorageUnit(this.bytes - that.bytes)
  def *(scala: Double): StorageUnit = new StorageUnit((this.bytes * scala).toLong)

  @inline
  override def equals(obj: Any): Boolean = {
    (obj: @switch) match {
      case vo: ValueObject => hashCode() == obj.hashCode()
      case _ => false
    }
  }
  override def hashCode(): Int = bytes.hashCode()

  override def toString: String = bytes + ".bytes"

  override def compare(that: StorageUnit): Int =
    if (bytes < that.bytes) -1
    else if (bytes > that.bytes) 1
    else 0

  def toHuman: String = {
    val prefix = "KMGTPE"
    var prefixIndex = -1
    var display = bytes.toDouble.abs
    while (display > 1126.0) {
      prefixIndex += 1
      display /= 1024.0
    }
    if (prefixIndex < 0) {
      "%d B".format(bytes)
    } else {
      "%.1f %ciB".format(display * bytes.signum, prefix.charAt(prefixIndex))
    }
  }
}

object StorageUnit {
  lazy val infinite = new StorageUnit(Long.MaxValue)

  private def factor(s: String): Long = {
    var lower = s.toLowerCase
    if (lower endsWith "s")
      lower = lower dropRight 1

    lower match {
      case "byte" => 1L
      case "kilobyte" => 1L << 10
      case "megabyte" => 1L << 20
      case "gigabyte" => 1L << 30
      case "terabyte" => 1L << 40
      case "petabyte" => 1L << 50
      case "exabyte" => 1L << 60
      case badUnit => throw new NumberFormatException(s"Unrecognized unit $badUnit")
    }
  }

  def parse(s: String): StorageUnit = s.split("\\.") match {
    case Array(v, u) =>
      val vv = v.toInt
      val uu = factor(u)
      new StorageUnit(vv * uu)
    case _ =>
      throw new NumberFormatException("invalid storage unit string")
  }
}
