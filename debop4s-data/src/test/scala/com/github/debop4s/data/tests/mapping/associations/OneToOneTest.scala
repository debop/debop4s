package com.github.debop4s.data.tests.mapping.associations

import com.github.debop4s.data.tests.AbstractJpaTest
import com.github.debop4s.data.model.HibernateEntity
import java.lang
import javax.persistence._
import com.github.debop4s.core.utils.Hashs
import org.junit.Test
import org.springframework.transaction.annotation.Transactional

/**
 * OneToOneTest
 * Created by debop on 2014. 3. 6.
 */
@Transactional
class OneToOneTest extends AbstractJpaTest {

    @PersistenceContext val em: EntityManager = null

    @Test
    def authorBiography() {}

    @Test
    def unidirectionalOneToOne() {
        val horse = new Horse()
        horse.name = "적토마"

        val cavalier = new Cavalier()
        cavalier.name = "관우"
        cavalier.horse = horse

        em.persist(horse)
        em.persist(cavalier)
        em.flush()
        em.clear()

        val loaded = em.find(classOf[Cavalier], cavalier.getId)
        assert(loaded != null)
        assert(loaded.horse != null)

        em.remove(loaded)
        em.remove(loaded.horse)
        em.flush()
        em.clear()

        assert(em.find(classOf[Cavalier], cavalier.getId) == null)
    }
}

@Entity
class Cavalier extends HibernateEntity[lang.Long] {

    @Id
    @GeneratedValue
    var id: lang.Long = _

    def getId = id

    var name: String = _

    @OneToOne
    @JoinColumn(name = "horseId")
    var horse: Horse = _

    override def hashCode(): Int = Hashs.compute(name)
}

@Entity
class Horse extends HibernateEntity[lang.Long] {

    @Id
    @GeneratedValue
    var id: lang.Long = _

    def getId = id

    var name: String = _

}
