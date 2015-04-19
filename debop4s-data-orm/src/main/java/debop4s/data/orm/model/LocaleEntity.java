package debop4s.data.orm.model;

import java.util.Locale;
import java.util.Set;

public interface LocaleEntity<TLocaleValue extends LocaleValue> extends PersistentObject {

    /**
     * 특정 지역에 해당하는 정보
     *
     * @param locale Locale 정보
     * @return 특정 지역에 해당하는 정보
     */
    TLocaleValue getLocaleValue(final Locale locale);

    /**
     * 엔티티가 보유한 지역 정보
     *
     * @return Locale Set
     */
    Set<Locale> getLocales();

    /**
     * 엔티티에 지역화 정보를 추가합니다.
     *
     * @param locale      지역 정보
     * @param localeValue 해당 지역에 해당하는 정보
     */
    void addLocaleValue(final Locale locale, final TLocaleValue localeValue);

    /**
     * 특정 지역의 정보를 제거합니다.
     *
     * @param locale 지역 정보
     */
    void removeLocaleValue(final Locale locale);

    /**
     * 특정 지역의 정보를 가져옵니다. 만약 해당 지역의 정보가 없다면 엔티티의 정보를 이용한 정보를 제공합니다.
     *
     * @param locale 지역 정보
     * @return 지역화 정보
     */
    TLocaleValue getLocaleValueOrDefault(final Locale locale);

    /**
     * 현 Thread Context 에 해당하는 지역의 정보를 제공합니다.
     *
     * @return 지역화 정보
     */
    TLocaleValue getCurrentLocaleValue();
}
