package debop4s.core.tools;

import com.google.common.collect.Lists;
import debop4s.core.Guard;
import debop4s.core.concurrent.JAsyncs;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import org.modelmapper.convention.MatchingStrategies;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * {@link org.modelmapper.ModelMapper} 를 이용하여, 객체간의 정보를 매핑합니다.
 *
 * @author 배성혁 ( sunghyouk.bae@gmail.com )
 * @since 12. 9. 20.
 */
@Slf4j
public final class MapperTool {

    private MapperTool() {
    }

    private static final ModelMapper mapper;

    static {
        log.debug("ModelMapper를 생성합니다...");

        mapper = new ModelMapper();
        mapper.getConfiguration()
              .setFieldMatchingEnabled(true)
              .setMatchingStrategy(MatchingStrategies.STANDARD)
              .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE);

        log.info("ModelMapper를 초기화했습니다.");
    }

    /**
     * 객체를 매핑을 통해 원하는 수형으로 변환합니다.
     *
     * @param source           the source
     * @param destinationClass the destination class
     * @return the t
     */
    public static <T> T createMap(final Object source, final Class<T> destinationClass) {
        Guard.shouldNotBeNull(source, "source");
        Guard.shouldNotBeNull(destinationClass, "destinationClass");

        return mapper.map(source, destinationClass);
    }

    /**
     * 객체를 매핑해서 원하는 수형으로 변환합니다.
     *
     * @param source      the source
     * @param destination the destination
     */
    public static void map(final Object source, final Object destination) {
        Guard.shouldNotBeNull(source, "source");
        Guard.shouldNotBeNull(destination, "destination");

        mapper.map(source, destination);
    }

    /**
     * 지정된 시퀀스의 모든 요소를 대상 수형으로 변환합니다.
     *
     * @param sources          the sources
     * @param destinationClass the destination class
     * @return the list
     */
    public static <S, T> List<T> mapList(final Iterable<S> sources, final Class<T> destinationClass) {
        List<T> destinations = Lists.newArrayList();

        for (S source : sources) {
            destinations.add(mapper.map(source, destinationClass));
        }
        return destinations;
    }

    /**
     * 비동기 방식으로 매핑을 수행합니다.
     *
     * @param source           the source
     * @param destinationClass the destination class
     * @return the future
     */
    public static <T> Future<T> mapAsync(final Object source, final Class<T> destinationClass) {
        return JAsyncs.startNew(new Callable<T>() {
            @Override
            public T call() throws Exception {
                return mapper.map(source, destinationClass);
            }
        });
    }

    /**
     * 비동기 방식으로 매핑을 수행합니다.
     *
     * @param sources          the sources
     * @param destinationClass the destination class
     * @return the future
     */
    public static <S, T> Future<List<T>> mapListAsync(final Iterable<S> sources, final Class<T> destinationClass) {
        return JAsyncs.startNew(new Callable<List<T>>() {
            @Override
            public List<T> call() throws Exception {
                return mapList(sources, destinationClass);
            }
        });
    }
}
