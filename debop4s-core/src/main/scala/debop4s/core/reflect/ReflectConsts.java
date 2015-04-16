package debop4s.core.reflect;

/**
 * Reflection 관련 상수
 *
 * @author 배성혁 ( sunghyouk.bae@gmail.com )
 * @since 13. 1. 21
 */
public class ReflectConsts {

    /** Reflection 관련 debop4s.redis.base package : debop4s.core.reflect */
    public static final String BASE_PACKAGE = ReflectConsts.class.getPackage().getName();

    /** Constructor Access Path */
    public static final String CONSTRUCTOR_ACCESS_PATH = ConstructorAccess.class.getName().replace(".", "/");

    /** Method Access Path */
    public static final String METHOD_ACCESS_PATH = MethodAccess.class.getName().replace(".", "/");

    /** Field Access Path */
    public static final String FIELD_ACCESS_PATH = FieldAccess.class.getName().replace(".", "/");
}
