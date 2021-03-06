package debop4s.core.reflect;

import com.google.common.collect.Lists;
import debop4s.core.Guard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.List;

/**
 * AccessClassLoader
 *
 * @author 배성혁 ( sunghyouk.bae@gmail.com )
 * @since 13. 1. 21
 */
class AccessClassLoader extends ClassLoader {

    private static final Logger log = LoggerFactory.getLogger(AccessClassLoader.class);

    private static final List<AccessClassLoader> accessClassLoaders = Lists.newArrayList();

    /** AccessClassLoger를 생성합니다. */
    static AccessClassLoader get(final Class type) {

        log.trace("AccessClassLoader를 생성합니다. kind=[{}]", type);
        ClassLoader parent = type.getClassLoader();

        // com.google.common.collect.Iterables 를 사용하여 변경해 보세요.
        synchronized (accessClassLoaders) {
            for (AccessClassLoader loader : accessClassLoaders) {
                if (loader.getParent() == parent)
                    return loader;
            }
            AccessClassLoader loader = new AccessClassLoader(parent);
            accessClassLoaders.add(loader);
            return loader;
        }
    }

    private AccessClassLoader(ClassLoader parent) {
        super(parent);
    }

    protected synchronized Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
        Guard.shouldNotBeEmpty(name, "name");
        if (name.equals(FieldAccess.class.getName())) return FieldAccess.class;
        if (name.equals(MethodAccess.class.getName())) return MethodAccess.class;
        if (name.equals(ConstructorAccess.class.getName())) return ConstructorAccess.class;

        //  all other classes come from the ClassLoader
        return super.loadClass(name, resolve);
    }

    Class<?> defineClass(final String name, final byte[] bytes) throws ClassFormatError {
        Guard.shouldNotBeEmpty(name, "name");
        Guard.shouldNotBeNull(bytes, "bytes");
        try {
            Method method = ClassLoader.class
                    .getDeclaredMethod("defineClass",
                                       new Class[] { String.class, byte[].class, int.class, int.class });
            method.setAccessible(true);

            // NOTE: 꼭 Integer.valueOf() 를 써야 합니다.
            //
            return (Class) method.invoke(getParent(), name, bytes, Integer.valueOf(0), Integer.valueOf(bytes.length));
        } catch (Exception ignored) {
        }
        return defineClass(name, bytes, 0, bytes.length);
    }
}
