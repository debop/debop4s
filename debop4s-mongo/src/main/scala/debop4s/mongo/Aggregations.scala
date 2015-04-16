package debop4s.mongo

import java.util.{List => JList}

import org.springframework.data.mongodb.core.aggregation.{Aggregation, AggregationOperation}
import org.springframework.data.mongodb.core.query.Criteria

import scala.annotation.varargs
import scala.collection.mutable

/**
 * MongoDB Aggregations 작업에 필요한 메소드를 제공합니다.
 *
 * @author Sunghyouk Bae
 */
object Aggregations {

  @varargs
  def addAggregationOperations(operations: JList[AggregationOperation], criterias: Criteria*) {
    criterias.foreach { crit =>
      operations.add(Aggregation.`match`(crit))
    }
  }

  @varargs
  def addAggregationOperations(operations: mutable.Buffer[AggregationOperation], criterias: Criteria*) {
    criterias.foreach { crit =>
      operations += Aggregation.`match`(crit)
    }
  }

  def buildAggregation(operations: JList[AggregationOperation]) = {
    Aggregation.newAggregation(operations)
  }

  @varargs
  def buildAggregation(operations: AggregationOperation*) = {
    Aggregation.newAggregation(operations: _*)
  }
}

