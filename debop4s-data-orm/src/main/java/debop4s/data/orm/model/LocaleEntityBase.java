package debop4s.data.orm.model;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * 지역화 정보를 가지는 엔티티의 기본 클래스입니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 8. 오후 12:58
 */
abstract public class LocaleEntityBase<TId, TLocaleValue extends LocaleValue>
        extends HibernateEntityBase<TId> implements LocaleEntity<TLocaleValue> {

    private TLocaleValue defaultLocaleValue = null;

    abstract public Map<Locale, TLocaleValue> getLocaleMap();

    public TLocaleValue getDefaultLocale() {
        if (defaultLocaleValue == null) {
            defaultLocaleValue = createDefaultLocaleValue();
        }
        return defaultLocaleValue;
    }

    /**
     * Java에서는 실행 시 Generic 수형을 없애버립니다.
     * scala나 c#은 generic으로 인스턴스를 생성할 수 있지만, Java는 불가능합니다.
     * 그래서 이 값을 꼭 구현해 주셔야 합니다.
     *
     * @return TLocalVal 인스턴스
     */
    abstract public TLocaleValue createDefaultLocaleValue();

    @Override
    public TLocaleValue getLocaleValue(final Locale locale) {
        return getLocaleValueOrDefault(locale);
    }

    @Override
    public Set<Locale> getLocales() {
        return getLocaleMap().keySet();
    }

    @Override
    public void addLocaleValue(final Locale locale, final TLocaleValue localeValue) {
        getLocaleMap().put(locale, localeValue);
    }

    @Override
    public void removeLocaleValue(final Locale locale) {
        getLocaleMap().remove(locale);
    }

    @Override
    public TLocaleValue getLocaleValueOrDefault(final Locale locale) {
        boolean notExists = getLocaleMap() == null ||
                            getLocaleMap().size() == 0 ||
                            locale == null ||
                            locale.getDisplayName() == null;
        if (notExists)
            return getDefaultLocale();
        else if (getLocaleMap().containsKey(locale))
            return getLocaleMap().get(locale);
        else
            return getDefaultLocale();
    }

    @Override
    public TLocaleValue getCurrentLocaleValue() {
        return getLocaleValueOrDefault(Locale.getDefault());
    }

    private static final long serialVersionUID = -2891849618560053560L;
}
