package com.github.debop4s.data.tests.mapping.associations

import com.github.debop4s.data.model.HibernateEntity
import com.github.debop4s.data.tests.AbstractJpaTest
import java.{lang, util}
import javax.persistence._
import org.junit.Test
import java.security.Timestamp
import com.github.debop4s.core.utils.Hashs
import org.hibernate.annotations.{Parent, LazyCollection, LazyCollectionOption}
import com.github.debop4s.core.ValueObject
import java.util.Date
import com.github.debop4s.data.tests.mapping.associations.ProductStatus.ProductStatus
import scala.beans.BeanProperty


/**
 * OneToManySetTest
 * Created by debop on 2014. 3. 6.
 */
@org.springframework.transaction.annotation.Transactional
class OneToManySetTest extends AbstractJpaTest {

    @PersistenceContext val em: EntityManager = null

    @Test
    def bidding() {

        val item = new BiddingItem()
        val bid1 = new Bid(item, BigDecimal(100.0))
        val bid2 = new Bid(item, BigDecimal(200.0))

        em.persist(item)
        em.flush()
        em.clear()

        val loaded = em.find(classOf[BiddingItem], item.id)
        assert(loaded != null)
        assert(loaded.bids.size == 2)

        val bid11 = loaded.bids.iterator().next()
        loaded.bids.remove(bid11)

        em.persist(loaded)
        em.flush()
        em.clear()

        val loaded2 = em.find(classOf[BiddingItem], item.id)
        assert(loaded2 != null)
        assert(loaded2.bids.size == 1)

        em.remove(loaded2)
        em.flush()

        assert(em.find(classOf[BiddingItem], item.id) == null)
    }

    @Test
    def productTest() {
        val item = new ProductItem()

        val image1 = new ProductImage()
        image1.name = "image1"
        image1.item = item
        item.images.add(image1)

        val image2 = new ProductImage()
        image2.name = "image1"
        image2.item = item
        item.images.add(image2)

        //item.status = ProductStatus.Active

        em.persist(item)
        em.flush()
        em.clear()

        val loaded = em.find(classOf[ProductItem], item.id)
        assert(loaded != null)
        assert(loaded.images.size == 2)

        loaded.images.clear()
        em.persist(loaded)
        em.flush()
        em.clear()

        val loaded2 = em.find(classOf[ProductItem], item.id)
        assert(loaded2 != null)
        assert(loaded2.images.size == 0)

        em.remove(loaded2)
        em.flush()
        assert(em.find(classOf[ProductItem], item.id) == null)


    }
}

@Entity
@Access(AccessType.FIELD)
class Bid extends HibernateEntity[lang.Long] {

    def this(item: BiddingItem, amount: BigDecimal) {
        this()
        this.item = item
        this.item.bids.add(this)
        this.amount = amount
    }

    @Id
    @GeneratedValue
    var id: lang.Long = _

    def getId = id

    @ManyToOne
    @JoinColumn(name = "itemId")
    var item: BiddingItem = _

    @Column(nullable = false)
    var amount: BigDecimal = _

    @Transient
    var timestamp: Timestamp = _

    override def hashCode(): Int = Hashs.compute(amount)
}

@Entity
@Access(AccessType.FIELD)
class BiddingItem extends HibernateEntity[lang.Long] {

    @Id
    @GeneratedValue
    var id: lang.Long = _

    def getId = id

    var name: String = _

    var description: String = _

    @OneToMany(mappedBy = "item", cascade = Array(CascadeType.ALL), orphanRemoval = true)
    @LazyCollection(value = LazyCollectionOption.EXTRA)
    var bids: util.Set[Bid] = new util.HashSet[Bid]()

    override def hashCode(): Int = Hashs.compute(name)
}

@Embeddable
@Access(AccessType.FIELD)
class ProductImage extends ValueObject {

    // 이 놈은 @BeanProperty가 없으면 예외가 발생합니다.
    @Parent
    @BeanProperty
    var item: ProductItem = _

    var name: String = _

    var filename: String = _

    var sizeX: Int = 0
    var sizeY: Int = 0

    override def hashCode(): Int = Hashs.compute(name, filename)
}

@Entity
@Access(AccessType.FIELD)
class ProductItem extends HibernateEntity[lang.Long] {

    @Id
    @GeneratedValue
    var id: lang.Long = _

    def getId = id

    var name: String = _
    var description: String = _
    var initalPrice: BigDecimal = _
    var reservePrice: BigDecimal = _

    @Temporal(TemporalType.DATE)
    var startDate: Date = _

    @Temporal(TemporalType.DATE)
    var endDate: Date = _

    @Column(name = "status")
    var statusInt: Int = _

    def status: ProductStatus = ProductStatus(statusInt)

    def status_=(v: ProductStatus.Value) { statusInt = v.id }

    @CollectionTable(name = "ProductItemImage", joinColumns = Array(new JoinColumn(name = "ProductItemId")))
    @ElementCollection(targetClass = classOf[ProductImage])
    @org.hibernate.annotations.Cascade(Array(org.hibernate.annotations.CascadeType.ALL))
    var images: util.Set[ProductImage] = new util.HashSet[ProductImage]()
}

object ProductStatus extends Enumeration {

    type ProductStatus = Value

    val Unknown = Value(0, "Unknown")
    val Active = Value(0, "Active")
    val Inactive = Value(0, "Inactive")
}