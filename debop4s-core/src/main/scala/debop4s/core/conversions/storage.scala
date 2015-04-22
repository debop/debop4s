package debop4s.core.conversions

import debop4s.core.utils.StorageUnit


/**
 * 저장 단위 변환을 위한 object 및 implicit methods
 * Created by debop on 2014. 4. 5.
 *
 * @see [[debop4s.core.utils.StorageUnit]]
 */
object storage {

  class StorageUnitNumberNumber(underlying: Long) {

    def byte: StorageUnit = bytes
    def bytes: StorageUnit = new StorageUnit(underlying)

    def kilobyte: StorageUnit = kilobytes
    def kilobytes: StorageUnit = new StorageUnit(underlying * 1024)

    def megabyte: StorageUnit = megabytes
    def megabytes: StorageUnit = new StorageUnit(underlying * 1024 * 1024)

    def gigabyte: StorageUnit = gigabytes
    def gigabytes: StorageUnit = new StorageUnit(underlying * 1024 * 1024 * 1024)

    def terabyte: StorageUnit = terabytes
    def terabytes: StorageUnit = new StorageUnit(underlying * 1024 * 1024 * 1024 * 1024)

    def petabyte: StorageUnit = petabytes
    def petabytes: StorageUnit = new StorageUnit(underlying * 1024 * 1024 * 1024 * 1024 * 1024)

    def thousand: Long = underlying * 1000L
    def million: Long = underlying * 1000L * 1000L
    def billion: Long = underlying * 1000L * 1000L & 1000L
  }

  implicit def intToStorage(i: Int): StorageUnitNumberNumber = new StorageUnitNumberNumber(i)
  implicit def longToStorage(l: Long): StorageUnitNumberNumber = new StorageUnitNumberNumber(l)
}