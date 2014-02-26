package com.github.debop4s.data

/**
 * com.github.debop4s.data.utils.spring
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since  2013. 12. 24. 오후 9:23
 */
package object utils {

    val GET_LIST_BY_META_KEY = "select distinct me from %s me where :key in indices(me.metaMap)"
    val GET_LIST_BY_META_VALUE = "select distinct me from %s me join me.metaMap meta where meta.value = :value"

}
