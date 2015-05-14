package debop4s.core.tools;

import debop4s.core.Guard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

/**
 * 리소스와 관련된 정보를 제공합니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 10. 21. 오후 3:13
 * @deprecated {@link debop4s.core.utils.Resources} 를 사용하세요.
 */
@Deprecated
public final class ResourceTool {

    private ResourceTool() {}

    private static final Logger log = LoggerFactory.getLogger(ResourceTool.class);

    /**
     * 지정한 경로의 리소스를 읽기위한 InputStream 을 반환합니다.
     *
     * @param path 경로
     * @return 리소스의 Input Stream. 해당 리소스가 없다면 null 을 반환한다.
     */
    public static InputStream getClassPathResourceStream(final String path) {
        log.debug("리소스 파일을 읽어드립니다. path=[{}]", path);
        Guard.shouldNotBeEmpty(path, "path");

        final String url = path.startsWith("/") ? path.substring(1) : path;
        return ResourceTool.class.getClassLoader().getResourceAsStream(url);
    }


    /**
     * 지정한 경로의 리소스를 읽기위한 InputStream 을 반환합니다.
     *
     * @param path        경로
     * @param classLoader 리소스를 로드할 Loader
     * @return 리소스의 Input Stream. 해당 리소스가 없다면 null 을 반환한다.
     */
    public static InputStream getClassPathResourceStream(final String path, final ClassLoader classLoader) {
        log.debug("리소스 파일을 읽어드립니다. path=[{}]", path);
        Guard.shouldNotBeEmpty(path, "path");
        Guard.shouldNotBeNull(classLoader, "classLoader");

        final String url = path.startsWith("/") ? path.substring(1) : path;
        return classLoader.getResourceAsStream(url);
    }
}
