package debop4s.core.time

import org.joda.time.Partial

object StaticPartial extends StaticPartial

trait StaticPartial {
    type Property = Partial.Property
}
