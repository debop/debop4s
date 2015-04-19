package debop4s.data.orm.hibernate.tools;

import debop4s.core.utils.Strings;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Criteria;
import org.hibernate.criterion.*;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * Criteria Tool
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 8. 오후 10:17
 * @deprecated use {@link debop4s.data.orm.hibernate.utils.CriteriaUtils}
 */
@Deprecated
@Slf4j
public final class CriteriaTool {

    private CriteriaTool() { }

    /**
     * 속성 값이 lo, hi 사이의 값인지를 검사하는 질의어
     *
     * @param propertyName 속성 명
     * @param lo           하한 값
     * @param hi           상한 값
     * @return the is between criterion
     */
    public static Criterion getBetween(String propertyName, Object lo, Object hi) {
        return getBetween(propertyName, lo, hi, true, true);
    }

    /**
     * 속성 값이 lo, hi 사이의 값인지를 검사하는 질의어
     *
     * @param propertyName 속성 명
     * @param lo           하한 값
     * @param hi           상한 값
     * @param includeLo    하한 값 포함 여부
     * @param includeHi    상한 값 포함 여부
     * @return the is between criterion
     */
    public static Criterion getBetween(String propertyName, Object lo, Object hi, boolean includeLo, boolean includeHi) {

        if (lo == null && hi == null)
            throw new IllegalArgumentException("상하한 값 모두 Null이면 안됩니다.");

        if (lo != null && hi != null)
            return Restrictions.between(propertyName, lo, hi);

        Conjunction result = Restrictions.conjunction();

        if (lo != null)
            result.add((includeLo) ? Restrictions.ge(propertyName, lo) : Restrictions.gt(propertyName, lo));

        if (hi != null)
            result.add((includeHi) ? Restrictions.le(propertyName, hi) : Restrictions.lt(propertyName, hi));

        return result;
    }

    /**
     * 지정한 값이 두 속성 값 사이에 존재하는지 여부
     *
     * @param loPropertyName the lo property name
     * @param hiPropertyName the hi property name
     * @param value          the value
     * @param includeLo      the include lo
     * @param includeHi      the include hi
     * @return the is in range criterion
     */
    public static Criterion getInRange(String loPropertyName,
                                       String hiPropertyName,
                                       Object value,
                                       boolean includeLo,
                                       boolean includeHi) {

        SimpleExpression loCriterion =
                (includeLo) ? Restrictions.le(loPropertyName, value) : Restrictions.lt(loPropertyName, value);
        SimpleExpression hiCriterion =
                (includeHi) ? Restrictions.ge(hiPropertyName, value) : Restrictions.gt(hiPropertyName, value);

        return Restrictions.conjunction()
                           .add(Restrictions.disjunction()
                                            .add(Restrictions.isNull(loPropertyName))
                                            .add(loCriterion))
                           .add(Restrictions.disjunction()
                                            .add(Restrictions.isNull(hiPropertyName))
                                            .add(hiCriterion));
    }

    /**
     * 지정한 범위 값이 두 속성 값 구간과 겹치는지를 알아보기 위한 질의어
     *
     * @param loPropertyName the lo property name
     * @param hiPropertyName the hi property name
     * @param lo             the lo
     * @param hi             the hi
     * @param includeLo      the include lo
     * @param includeHi      the include hi
     * @return the is overlap criterion
     */
    public static Criterion getOverlap(String loPropertyName,
                                       String hiPropertyName,
                                       Object lo,
                                       Object hi,
                                       boolean includeLo,
                                       boolean includeHi) {
        if (lo == null && hi == null)
            throw new IllegalArgumentException("lo, hi 값 모두 null 이면 질의어를 만들 수 없습니다.");

        if (lo != null && hi != null) {
            return
                    Restrictions
                            .disjunction()
                            .add(getInRange(loPropertyName, hiPropertyName, lo, includeLo, includeHi))
                            .add(getInRange(loPropertyName, hiPropertyName, hi, includeLo, includeHi))
                            .add(getBetween(loPropertyName, lo, hi, includeLo, includeHi))
                            .add(getBetween(hiPropertyName, lo, hi, includeLo, includeHi));
        } else if (lo != null) {
            return
                    Restrictions
                            .disjunction()
                            .add(getInRange(loPropertyName, hiPropertyName, lo, includeLo, includeHi))
                            .add((includeLo) ? Restrictions.ge(loPropertyName, lo) : Restrictions.gt(loPropertyName, lo))
                            .add((includeLo) ? Restrictions.ge(hiPropertyName, lo) : Restrictions.gt(hiPropertyName, lo));
        } else {
            return
                    Restrictions
                            .disjunction()
                            .add(getInRange(loPropertyName, hiPropertyName, hi, includeLo, includeHi))
                            .add((includeHi) ? Restrictions.le(loPropertyName, hi) : Restrictions.lt(loPropertyName, hi))
                            .add((includeHi) ? Restrictions.le(hiPropertyName, hi) : Restrictions.lt(hiPropertyName, hi));
        }
    }

    /**
     * value가 null 이 아니면, 속성값과 eq 이거나 null 인 경우 모두 구한다. value가 null 인 경우는 isNull 로 만든다.
     *
     * @param propertyName the property name
     * @param value        the value
     * @return the eq include null
     */
    public static Criterion getEqIncludeNull(String propertyName, Object value) {
        return (value == null)
                ? Restrictions.isNull(propertyName)
                : Restrictions.eqOrIsNull(propertyName, value);
    }

    /**
     * 대소문자 구분 없이 LIKE 를 수행합니다.
     *
     * @param propertyName 속성명
     * @param value        LIKE 검색할 값
     * @return Criterion
     */
    public static Criterion getInsensitiveLikeIncludeNull(String propertyName, String value) {
        return getInsensitiveLikeIncludeNull(propertyName, value, MatchMode.START);
    }

    /**
     * 대소문자 구분 없이 LIKE 를 수행합니다.
     *
     * @param propertyName 속성명
     * @param value        LIKE 검색할 값
     * @param matchMode    LIKE 검색 방법 ({@link MatchMode})
     * @return Criterion
     */
    public static Criterion getInsensitiveLikeIncludeNull(String propertyName,
                                                          String value,
                                                          MatchMode matchMode) {
        if (Strings.isWhitespace(propertyName))
            return Restrictions.isEmpty(propertyName);
        else
            return Restrictions.disjunction()
                               .add(Restrictions.ilike(propertyName, value, matchMode))
                               .add(Restrictions.isEmpty(propertyName));
    }

    /**
     * Criteria 에 속성명 = value 질의를 추가합니다.
     *
     * @param criteria     criteria
     * @param propertyName 속성명
     * @param value        value
     * @return criteria
     */
    public static Criteria addEq(Criteria criteria, String propertyName, Object value) {
        return criteria.add(Restrictions.eq(propertyName, value));
    }

    public static Criteria addEqOrIsNull(Criteria criteria, String propertyName, Object value) {
        return criteria.add(Restrictions.eqOrIsNull(propertyName, value));
    }

    public static Criteria addNotEq(Criteria criteria, String propertyName, Object value) {
        return criteria.add(Restrictions.not(Restrictions.eq(propertyName, value)));
    }

    public static Criteria addLe(Criteria criteria, String propertyName, Object value) {
        return criteria.add(Restrictions.le(propertyName, value));
    }

    public static Criteria addLeProperty(Criteria criteria, String propertyName, String otherPropertyName) {
        return criteria.add(Restrictions.leProperty(propertyName, otherPropertyName));
    }

    public static Criteria addLt(Criteria criteria, String propertyName, Object value) {
        return criteria.add(Restrictions.lt(propertyName, value));
    }

    public static Criteria addLtProperty(Criteria criteria, String propertyName, String otherPropertyName) {
        return criteria.add(Restrictions.ltProperty(propertyName, otherPropertyName));
    }

    public static Criteria addGe(Criteria criteria, String propertyName, Object value) {
        return criteria.add(Restrictions.ge(propertyName, value));
    }

    public static Criteria addGeProperty(Criteria criteria, String propertyName, String otherPropertyName) {
        return criteria.add(Restrictions.geProperty(propertyName, otherPropertyName));
    }

    public static Criteria addGt(Criteria criteria, String propertyName, Object value) {
        return criteria.add(Restrictions.gt(propertyName, value));
    }

    public static Criteria addGtProperty(Criteria criteria, String propertyName, String otherPropertyName) {
        return criteria.add(Restrictions.gtProperty(propertyName, otherPropertyName));
    }

    public static Criteria addAllEq(Criteria criteria, Map<String, ?> propertyNameValues) {
        return criteria.add(Restrictions.allEq(propertyNameValues));
    }

    public static Criteria addIsEmpty(Criteria criteria, String propertyName) {
        return criteria.add(Restrictions.isEmpty(propertyName));
    }

    public static Criteria addIsNotEmpty(Criteria criteria, String propertyName) {
        return criteria.add(Restrictions.isNotEmpty(propertyName));
    }

    public static Criteria addIsNull(Criteria criteria, String propertyName) {
        return criteria.add(Restrictions.isNull(propertyName));
    }

    public static Criteria addIsNotNull(Criteria criteria, String propertyName) {
        return criteria.add(Restrictions.isNotNull(propertyName));
    }

    public static Criteria addLike(Criteria criteria, String propertyName, String value) {
        return addLike(criteria, propertyName, value, MatchMode.START);
    }

    public static Criteria addLike(Criteria criteria, String propertyName, String value, MatchMode matchMode) {
        return criteria.add(Restrictions.like(propertyName, value, matchMode));
    }

    public static Criteria addILike(Criteria criteria, String propertyName, String value) {
        return addILike(criteria, propertyName, value, MatchMode.START);
    }

    public static Criteria addILike(Criteria criteria, String propertyName, String value, MatchMode matchMode) {
        return criteria.add(Restrictions.ilike(propertyName, value, matchMode));
    }

    public static Criteria addIdEq(Criteria criteria, Object idValue) {
        return criteria.add(Restrictions.idEq(idValue));
    }

    public static Criteria addIn(Criteria criteria, String propertyName, Collection<?> ids) {
        return criteria.add(Restrictions.in(propertyName, ids));
    }

    public static Criteria addIn(Criteria criteria, String propertyName, Object[] ids) {
        return criteria.add(Restrictions.in(propertyName, ids));
    }

    /**
     * 속성명이 상하한 값 사이에 있는지 검사흐는 질의를 추가합니다. SQL의 BETWEEN과 같다.
     *
     * @param criteria     criteria
     * @param propertyName 속성 명
     * @param lo           하한 값
     * @param hi           상한 값
     * @param includeLo    하한 값 포함 여부
     * @param includeHi    상한 값 포함 여부
     * @return Criteria
     */
    public static Criteria addBetween(Criteria criteria,
                                      String propertyName,
                                      Object lo,
                                      Object hi,
                                      boolean includeLo,
                                      boolean includeHi) {
        return criteria.add(getBetween(propertyName, lo, hi, includeLo, includeHi));
    }

    /**
     * 두 속성 값이 지정한 값의 범위에 속하는지 여부
     *
     * @param criteria       criteria
     * @param loPropertyName 하한 값을 나타내는 속성
     * @param hiPropertyName 상한 값을 나타내는 속성
     * @param value          검사할 값
     * @param includeLo      하한 값 포함 여부
     * @param includeHi      상한 값 포함 여부
     * @return Criteria
     */
    public static Criteria addInRange(Criteria criteria,
                                      String loPropertyName,
                                      String hiPropertyName,
                                      Object value,
                                      boolean includeLo,
                                      boolean includeHi) {
        return criteria.add(getInRange(loPropertyName, hiPropertyName, value, includeLo, includeHi));
    }

    /**
     * Database 의 상하한 값과 질의를 하는 상하한 값이 겹치는 부분이 있는지 검사하는 질의어를 만듭니다.
     *
     * @param criteria       criteria
     * @param loPropertyName 하한 값을 나타내는 속성
     * @param hiPropertyName 상한 값을 나타내는 속성
     * @param lo             하한 값
     * @param hi             상한 값
     * @param includeLo      하한 값 포함 여부
     * @param includeHi      상한 값 포함 여부
     * @return Criteria
     */
    public static Criteria addOverlap(Criteria criteria,
                                      String loPropertyName,
                                      String hiPropertyName,
                                      Object lo,
                                      Object hi,
                                      boolean includeLo,
                                      boolean includeHi) {
        return criteria.add(getOverlap(loPropertyName, hiPropertyName, lo, hi, includeLo, includeHi));
    }

    /** 지정된 시각 이전인 경우 */
    public static Criteria addElapsed(Criteria criteria, String propertyName, Date moment) {
        return criteria.add(Restrictions.lt(propertyName, moment));
    }

    /** 지정된 시각 이후인 경우 */
    public static Criteria addFutures(Criteria criteria, String propertyName, Date moment) {
        return criteria.add(Restrictions.gt(propertyName, moment));
    }


    /** 속성 값이 null인 경우는 false로 간주하고, value와 같은 값을 가지는 질의어를 추가합니다. */
    public static Criteria addNullAsFalse(Criteria criteria, String propertyName, Boolean value) {
        return (value == null || value)
                ? addEq(criteria, propertyName, true)
                : criteria.add(getEqIncludeNull(propertyName, false));
    }

    /** 속성 값이 null 인 경우는 true로 간주하고, value와 같은 값을 가지는 질의어를 추가합니다. */
    public static Criteria addNullAsTrue(Criteria criteria, String propertyName, Boolean value) {
        if (value == null)
            value = false;

        return (value)
                ? addEq(criteria, propertyName, false)
                : criteria.add(getEqIncludeNull(propertyName, true));
    }

    public static Criteria addNot(Criteria criteria, Criterion expression) {
        return criteria.add(Restrictions.not(expression));
    }

    public static DetachedCriteria addEq(DetachedCriteria dc, String propertyName, Object value) {
        return dc.add(Restrictions.eq(propertyName, value));
    }


    public static DetachedCriteria addEqOrIsNull(DetachedCriteria dc, String propertyName, Object value) {
        return dc.add(Restrictions.eqOrIsNull(propertyName, value));
    }

    public static DetachedCriteria addNotEq(DetachedCriteria dc, String propertyName, Object value) {
        return dc.add(Restrictions.not(Restrictions.eq(propertyName, value)));
    }

    public static DetachedCriteria addLe(DetachedCriteria dc, String propertyName, Object value) {
        return dc.add(Restrictions.le(propertyName, value));
    }

    public static DetachedCriteria addLeProperty(DetachedCriteria dc, String propertyName, String otherPropertyName) {
        return dc.add(Restrictions.leProperty(propertyName, otherPropertyName));
    }

    public static DetachedCriteria addLt(DetachedCriteria dc, String propertyName, Object value) {
        return dc.add(Restrictions.lt(propertyName, value));
    }

    public static DetachedCriteria addLtProperty(DetachedCriteria dc, String propertyName, String otherPropertyName) {
        return dc.add(Restrictions.ltProperty(propertyName, otherPropertyName));
    }

    public static DetachedCriteria addGe(DetachedCriteria dc, String propertyName, Object value) {
        return dc.add(Restrictions.ge(propertyName, value));
    }

    public static DetachedCriteria addGeProperty(DetachedCriteria dc, String propertyName, String otherPropertyName) {
        return dc.add(Restrictions.geProperty(propertyName, otherPropertyName));
    }

    public static DetachedCriteria addGt(DetachedCriteria dc, String propertyName, Object value) {
        return dc.add(Restrictions.gt(propertyName, value));
    }

    public static DetachedCriteria addGtProperty(DetachedCriteria dc, String propertyName, String otherPropertyName) {
        return dc.add(Restrictions.gtProperty(propertyName, otherPropertyName));
    }

    public static DetachedCriteria addAllEq(DetachedCriteria dc, Map<String, ?> propertyNameValues) {
        return dc.add(Restrictions.allEq(propertyNameValues));
    }

    public static DetachedCriteria addIsEmpty(DetachedCriteria dc, String propertyName) {
        return dc.add(Restrictions.isEmpty(propertyName));
    }

    public static DetachedCriteria addIsNotEmpty(DetachedCriteria dc, String propertyName) {
        return dc.add(Restrictions.isNotEmpty(propertyName));
    }

    public static DetachedCriteria addIsNull(DetachedCriteria dc, String propertyName) {
        return dc.add(Restrictions.isNull(propertyName));
    }

    public static DetachedCriteria addIsNotNull(DetachedCriteria dc, String propertyName) {
        return dc.add(Restrictions.isNotNull(propertyName));
    }

    public static DetachedCriteria addLike(DetachedCriteria dc, String propertyName, String value) {
        return addLike(dc, propertyName, value, MatchMode.START);
    }

    public static DetachedCriteria addLike(DetachedCriteria dc, String propertyName, String value, MatchMode matchMode) {
        return dc.add(Restrictions.like(propertyName, value, matchMode));
    }

    public static DetachedCriteria addILike(DetachedCriteria dc, String propertyName, String value) {
        return addILike(dc, propertyName, value, MatchMode.START);
    }

    public static DetachedCriteria addILike(DetachedCriteria dc, String propertyName, String value, MatchMode matchMode) {
        return dc.add(Restrictions.ilike(propertyName, value, matchMode));
    }

    public static DetachedCriteria addIdEq(DetachedCriteria dc, Object idValue) {
        return dc.add(Restrictions.idEq(idValue));
    }

    public static DetachedCriteria addIn(DetachedCriteria dc, String propertyName, Collection<?> ids) {
        return dc.add(Restrictions.in(propertyName, ids));
    }

    public static DetachedCriteria addIn(DetachedCriteria dc, String propertyName, Object[] ids) {
        return dc.add(Restrictions.in(propertyName, ids));
    }

    public static DetachedCriteria addBetween(DetachedCriteria dc,
                                              String propertyName,
                                              Object lo,
                                              Object hi,
                                              boolean includeLo,
                                              boolean includeHi) {
        return dc.add(getBetween(propertyName, lo, hi, includeLo, includeHi));
    }

    public static DetachedCriteria addInRange(DetachedCriteria dc,
                                              String loPropertyName,
                                              String hiPropertyName,
                                              Object value,
                                              boolean includeLo,
                                              boolean includeHi) {
        return dc.add(getInRange(loPropertyName, hiPropertyName, value, includeLo, includeHi));
    }

    public static DetachedCriteria addOverlap(DetachedCriteria dc,
                                              String loPropertyName,
                                              String hiPropertyName,
                                              Object lo,
                                              Object hi) {
        return addOverlap(dc, loPropertyName, hiPropertyName, lo, hi, true, true);
    }

    public static DetachedCriteria addOverlap(DetachedCriteria dc,
                                              String loPropertyName,
                                              String hiPropertyName,
                                              Object lo,
                                              Object hi,
                                              boolean includeLo,
                                              boolean includeHi) {
        return dc.add(getOverlap(loPropertyName, hiPropertyName, lo, hi, includeLo, includeHi));
    }

    /** 지정된 시각 이전인 경우 */
    public static DetachedCriteria addElapsed(DetachedCriteria dc, String propertyName, Date moment) {
        return dc.add(Restrictions.lt(propertyName, moment));
    }

    /** 지정된 시각 이후인 경우 */
    public static DetachedCriteria addFutures(DetachedCriteria dc, String propertyName, Date moment) {
        return dc.add(Restrictions.gt(propertyName, moment));
    }


    /** 속성 값이 null 이면 false 로 간주하고 value와 같은 값을 찾습니다. */
    public static DetachedCriteria addNullAsFalse(DetachedCriteria dc, String propertyName, Boolean value) {
        if (value == null)
            value = true;

        return (value)
                ? addEq(dc, propertyName, true)
                : dc.add(getEqIncludeNull(propertyName, false));
    }

    /** 속성 값이 null 이면 true로 간주하고 value와 같은 값을 찾습니다. */
    public static DetachedCriteria addNullAsTrue(DetachedCriteria dc, String propertyName, Boolean value) {
        if (value == null)
            value = true;

        return (value)
                ? addEq(dc, propertyName, false)
                : dc.add(getEqIncludeNull(propertyName, true));
    }

    /** 질의에 NOT 을 추가합니다. */
    public static DetachedCriteria addNot(DetachedCriteria dc, Criterion expression) {
        return dc.add(Restrictions.not(expression));
    }
}
