package debop4s.data.orm.jpa.mysql.service

import java.lang.{Long => JLong}
import java.util.{List => JList}

import debop4s.data.orm.jpa.mysql.model.{MySqlOrder, MySqlOrderItem}
import debop4s.data.orm.jpa.mysql.repository.{MySqlOrderItemRepository, MySqlOrderRepository}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

/**
 * MySqlOrderService
 * @author Sunghyouk Bae sunghyouk.bae@gmail.com
 */
@Component
@Transactional
class MySqlOrderService {

  @Autowired val orderRepo: MySqlOrderRepository = null
  @Autowired val orderItemRepo: MySqlOrderItemRepository = null

  def saveOrder(order: MySqlOrder): MySqlOrder = {
    orderRepo.save(order)
  }

  def saveOrderItem(orderItem: MySqlOrderItem): MySqlOrderItem = {
    orderItemRepo.save(orderItem)
  }

  def saveOrderItems(orderItems: JList[MySqlOrderItem]): Unit = {
    orderItemRepo.save(orderItems)
  }

  @Transactional(readOnly = true)
  def getOrder(orderId: Integer): MySqlOrder = {
    orderRepo.findOne(orderId)
  }

  @Transactional(readOnly = true)
  def getOrderByJPQL(orderId: Integer): MySqlOrder = {
    orderRepo.findById(orderId)
  }

  @Transactional(readOnly = true)
  def getOrderItems(orderId: Integer): JList[MySqlOrderItem] = {
    orderItemRepo.findByOrder(orderRepo.getOne(orderId))
  }

  @Transactional(readOnly = true)
  def getOrderItemsJpql(orderId: Integer): JList[MySqlOrderItem] = {
    orderItemRepo.findByOrderJpql(orderId)
  }

  @Transactional(readOnly = true)
  def getOrderItemsJpqlWithOrder(orderId: Integer): JList[MySqlOrderItem] = {
    orderItemRepo.findByOrderJpqlWithOrder(orderId)
  }
}
