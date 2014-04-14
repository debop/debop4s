package debop4s.core.jodatime

import org.joda.time.Partial

object JodaPartial extends JodaPartial

trait JodaPartial {
    type Property = Partial.Property
}
