package debop4s.core.reflect;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;

/**
 * {@link DynamicAccessor} 의 생성자입니다.
 *
 * @author 배성혁 ( sunghyouk.bae@gmail.com )
 * @since 13. 1. 21
 */
public class DynamicAccessorFactory {

    private static final Logger log = LoggerFactory.getLogger(DynamicAccessorFactory.class);

    private static final CacheLoader<Class<?>, DynamicAccessor> loader;
    private static final LoadingCache<Class<?>, DynamicAccessor> cache;

    static {
        log.info("DynamicAccessor 캐시를 생성합니다.");

        loader = new CacheLoader<Class<?>, DynamicAccessor>() {
            @Override
            @SuppressWarnings("unchecked")
            public DynamicAccessor<?> load(Class<?> type) throws Exception {
                return new DynamicAccessor(type);
            }
        };

        cache = CacheBuilder.newBuilder().weakValues().maximumSize(2000).build(loader);

        log.info("DynamicAccessor 캐시를 생성했습니다.");
    }

    /**
     * Create dynamic accessor.
     *
     * @param targetType the target kind
     * @return the dynamic accessor
     */
    @SuppressWarnings("unchecked")
    public static <T> DynamicAccessor<T> create(final Class<T> targetType) {
        try {
            return (DynamicAccessor<T>) cache.get(targetType);
        } catch (ExecutionException e) {
            log.error("DynamicAccessor 를 생성하는데 실패했습니다. targetType=" + targetType.getName(), e);
            return null;
        }
    }

    /** Clear void. */
    public static synchronized void clear() {
        cache.cleanUp();
    }
}
