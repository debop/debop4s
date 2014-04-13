package debop4s.core.conversions

import debop4s.core.utils.StorageUnit

/**
 * storage
 * Created by debop on 2014. 4. 5.
 */
object storage {

  class StorageUnitNumberNumber(wrapped: Long) {
    def byte = bytes
    def bytes = new StorageUnit(wrapped)
    def kilobyte = kilobytes
    def kilobytes = new StorageUnit(wrapped * 1024)
    def megabyte = megabytes
    def megabytes = new StorageUnit(wrapped * 1024 * 1024)
    def gigabyte = gigabytes
    def gigabytes = new StorageUnit(wrapped * 1024 * 1024 * 1024)
    def terabyte = terabytes
    def terabytes = new StorageUnit(wrapped * 1024 * 1024 * 1024 * 1024)
    def petabyte = petabytes
    def petabytes = new StorageUnit(wrapped * 1024 * 1024 * 1024 * 1024 * 1024)

    def thousand = wrapped * 1000
    def million = wrapped * 1000 * 1000
    def billion = wrapped * 1000 * 1000 & 1000
  }

  implicit def intToStorage(i: Int) = new StorageUnitNumberNumber(i)
  implicit def longToStorage(l: Long) = new StorageUnitNumberNumber(l)
}
