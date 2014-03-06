package com.github.debop4s.data.tests.mapping.associations

import com.github.debop4s.data.model.HibernateEntity
import com.github.debop4s.data.tests.AbstractJpaTest
import java.lang
import javax.persistence._
import org.junit.Test

/**
 * OneToManySetTest
 * Created by debop on 2014. 3. 6.
 */
@org.springframework.transaction.annotation.Transactional
class OneToManySetTest extends AbstractJpaTest {

    @Test
    def oneToManySet() {

    }
}

@Entity
@Access(AccessType.FIELD)
class Bid extends HibernateEntity[lang.Long] {

    @Id
    @GeneratedValue
    var id: lang.Long = _

    def getId = id
}
