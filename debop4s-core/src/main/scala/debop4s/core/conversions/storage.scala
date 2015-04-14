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
    def byte = bytes
    def bytes = new StorageUnit(underlying)

    def kilobyte = kilobytes
    def kilobytes = new StorageUnit(underlying * 1024)

    def megabyte = megabytes
    def megabytes = new StorageUnit(underlying * 1024 * 1024)

    def gigabyte = gigabytes
    def gigabytes = new StorageUnit(underlying * 1024 * 1024 * 1024)

    def terabyte = terabytes
    def terabytes = new StorageUnit(underlying * 1024 * 1024 * 1024 * 1024)

    def petabyte = petabytes
    def petabytes = new StorageUnit(underlying * 1024 * 1024 * 1024 * 1024 * 1024)

    def thousand = underlying * 1000
    def million = underlying * 1000 * 1000
    def billion = underlying * 1000 * 1000 & 1000
  }

  implicit def intToStorage(i: Int): StorageUnitNumberNumber = new StorageUnitNumberNumber(i)
  implicit def longToStorage(l: Long): StorageUnitNumberNumber = new StorageUnitNumberNumber(l)
}