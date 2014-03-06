package com.github.debop4s.data.model

import com.github.debop4s.core.ValueObject
import scala.collection.mutable

/**
 * 메타 정보를 가지는 엔티티
 *
 * @author sunghyouk.bae@gmail.com
 * @since 2014. 2. 28.
 */
trait HibernateMetaEntity[TId] extends HibernateEntity[TId] {

    // 이 값을 override 해서 Mapping을 수행해야 합니다.
    private val metaMap = mutable.LinkedHashMap[String, MetaValue]()

    def metaValue(key: String) = metaMap.get(key)

    def metaKeys() = metaMap.keys

    def addMeta(key: String, metaValue: MetaValue) {
        metaMap.put(key, metaValue)
    }

    def addMeta(key: String, value: String) {
        addMeta(key, MetaValue(value))
    }

    def removeMeta(key: String) {
        metaMap.remove(key)
    }
}


@SerialVersionUID(2290859959720539740L)
class MetaValue(var value: String,
                var label: String,
                var description: String,
                var exAttr: String) extends ValueObject {
}

object MetaValue {

    def apply(value: String, label: String = null, description: String = null, exAttr: String = null): MetaValue = {
        new MetaValue(value, label, description, exAttr)
    }
}

