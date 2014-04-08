package debop4s.core.utils

import debop4s.core._
import java.net.{Inet4Address, UnknownHostException, InetAddress}

/**
 * NetUtil
 * Created by debop on 2014. 4. 5.
 */
object NetUtil {

    def isIpv4Address(ip: String): Boolean = ipToOptionInt(ip).isDefined

    /**
     * ip address 가 private 인지 여부
     * 10.*.*.*, 172.16.*.*, 192.168.*.* 인 경우가 private 이다
     */
    def isPrivateAddress(ip: InetAddress): Boolean = ip match {
        case ip: Inet4Address =>
            val addr = ip.getAddress
            if (addr(0) == 10.toByte) true // 10/8
            else if (addr(0) == 172.toByte && (addr(1) & 0xf0) == 16.toByte) true // 172/12
            else if (addr(0) == 192.toByte && addr(1) == 168.toByte) true // 192.168/16
            else false
        case _ => false
    }

    /**
     * ip v4 문자열을 `Int`로 변환합니다.
     */
    def ipToInt(ip: String): Int =
        ipToOptionInt(ip) getOrElse {
            throw new IllegalArgumentException(s"Invalid IPv4 address: $ip")
        }

    /**
     * ip v4 문자열을 `Option[Int]`로 변환합니다.
     */
    def ipToOptionInt(ip: String): Option[Int] = {
        val dot1 = ip.indexOf('.')
        if (dot1 <= 0) return None

        val dot2 = ip.indexOf('.', dot1 + 1)
        if (dot2 == -1) return None

        val dot3 = ip.indexOf('.', dot2 + 1)
        if (dot3 == -1) return None

        val num1 = ipv4DecimalToInt(ip.substring(0, dot1))
        if (num1 < 0) return None

        val num2 = ipv4DecimalToInt(ip.substring(dot1 + 1, dot2))
        if (num2 < 0) return None

        val num3 = ipv4DecimalToInt(ip.substring(dot2 + 1, dot3))
        if (num3 < 0) return None

        val num4 = ipv4DecimalToInt(ip.substring(dot3 + 1))
        if (num4 < 0) return None

        Some((num1 << 24) | (num2 << 16) | (num3 << 8) | num4)
    }

    private[this] def ipv4DecimalToInt(s: String): Int = {
        if (s.isWhitespace || s.length > 3) return -1

        var i = 0
        var num = 0
        while (i < s.length) {
            val c = s.charAt(i).toInt
            if (c < '0' || c > '9') return -1
            num = (num * 10) + (c - '0')
            i += 1
        }
        if (num >= 0 && num <= 255) num else -1
    }

    def inetAddressToInt(inetAddress: InetAddress): Int = {
        inetAddress match {
            case inetAddress: Inet4Address =>
                val addr = inetAddress.getAddress
                ((addr(0) & 0xff) << 24) |
                ((addr(1) & 0xff) << 16) |
                ((addr(2) & 0xff) << 8) |
                (addr(3) & 0xff)
            case _ =>
                throw new IllegalArgumentException("non-Inet4Address cannot be converted to an Int")
        }
    }

    /**
     * Converts either a full or partial ip, (e.g.127.0.0.1, 127.0)
     * to it's integer equivalent with mask specified by prefixlen.
     * Assume missing bits are 0s for a partial ip. Result returned as
     * (ip, netMask)
     */
    def ipToIpBlock(ip: String, prefixLen: Option[Int]): (Int, Int) = {
        val arr = ip.split('.')
        val pLen = prefixLen match {
            case None if arr.size != 4 => arr.size * 8
            case t => t.getOrElse(32)
        }
        val netIp = ipToInt(arr.padTo(4, "0").mkString("."))
        val mask = (1 << 31) >> (pLen - 1)
        (netIp, mask)
    }

    def cidrToIpBlock(cidr: String): (Int, Int) = cidr.split('/') match {
        case Array(ip, prefixLen) => ipToIpBlock(ip, Some(prefixLen.toInt))
        case Array(ip) => ipToIpBlock(ip, None)
    }

    def isIpInBlock(ip: Int, ipBlock: (Int, Int)): Boolean = ipBlock match {
        case (netIp, mask) => (mask & ip) == netIp
    }

    //  def isInetAddressInBlock(inetAddress:InetAddress, ipBlock:(Int, Int)): Boolean =
    //    isInetAddressInBlock(inetAddress, ipBlock)

    def isIpInBlocks(ip: Int, ipBlocks: Iterable[(Int, Int)]): Boolean = {
        ipBlocks exists {
            ipBlock =>
                isIpInBlock(ip, ipBlock)
        }
    }

    def isInetAddressInBlocks(inetAddress: InetAddress, ipBlocks: Iterable[(Int, Int)]): Boolean =
        isIpInBlocks(inetAddressToInt(inetAddress), ipBlocks)

    def getLocalHost: InetAddress = InetAddress.getLocalHost

    def getLocalHostName: String = {
        try {
            InetAddress.getLocalHost.getHostName
        } catch {
            case uhe: UnknownHostException =>
                Option(uhe.getMessage) match {
                    case Some(host) =>
                        host.split(":") match {
                            case Array(hostName, _) => hostName
                            case _ => "unknown_host"
                        }
                    case None => "unknown_host"
                }
        }
    }
}
