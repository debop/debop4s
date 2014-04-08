package debop4s.core.time

import org.joda.time.Partial

object JodaPartial extends JodaPartial

trait JodaPartial {
    type Property = Partial.Property
}
