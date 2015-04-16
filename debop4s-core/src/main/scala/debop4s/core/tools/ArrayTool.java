package debop4s.core.tools;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import debop4s.core.Guard;
import debop4s.core.cryptography.Cryptos;
import debop4s.core.utils.Strings;
import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Array 관련 Utility class 입니다.
 *
 * @author 배성혁 ( sunghyouk.bae@gmail.com )
 * @since 12. 9. 14
 */
@Slf4j
@SuppressWarnings("unchecked")
public final class ArrayTool {

    private ArrayTool() { }

    /** 빈 바이트 배열 */
    public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];

    /** 빈 문자열 배열 */
    public static final String[] EMPTY_STRING_ARRAY = new String[0];

    /** 빈 int 배열 */
    public static final int[] EMPTY_INT_ARRAY = new int[0];

    /** 빈 long 배열 */
    public static final long[] EMPTY_LONG_ARRAY = new long[0];

    /**
     * 지졍된 배열이 null 이거나 빈 배열이면 true를 반환한다.
     *
     * @param array 검사할 배열
     * @return 배열이 null이거나 빈 배열이면 true, 값이 있으면 false를 반환한다.
     */
    public static <T> boolean isEmpty(final T[] array) {
        return ((array == null) || (array.length == 0));
    }

    /**
     * 컬렉션이 비었으면 true, 아니면 false를 반환
     *
     * @param iterable the iterable
     * @return 컬렉션이 null이거나 빈 배열이면 true, 값이 있으면 false를 반환한다.
     */
    public static <T> boolean isEmpty(final Iterable<? extends T> iterable) {
        return (iterable == null) || (!iterable.iterator().hasNext());
    }

    /**
     * 바이트 배열이 비었는지 검사
     *
     * @param array byte 배열
     * @return the boolean
     */
    public static boolean isEmpty(final byte[] array) {
        return ((array == null) || (array.length == 0));
    }

    /**
     * char 배열이 비었는지 검사
     *
     * @param array the array
     * @return the boolean
     */
    public static boolean isEmpty(final char[] array) {
        return ((array == null) || (array.length == 0));
    }

    /**
     * int 배열이 비었는지 검사
     *
     * @param array the array
     * @return the boolean
     */
    public static boolean isEmpty(final int[] array) {
        return ((array == null) || (array.length == 0));
    }

    /**
     * long 수형의 배열이 비었는지 검사
     *
     * @param array the array
     * @return the boolean
     */
    public static boolean isEmpty(final long[] array) {
        return ((array == null) || (array.length == 0));
    }

    /**
     * 배열에 지정한 항목이 존재하는지 검사
     *
     * @param array  검사할 배열
     * @param target 포함 여부를 검사할 항목
     * @return 배열에 항목을 포함하면 true, 아니면 false
     */
    public static <T> boolean contains(final T[] array, final T target) {
        Guard.shouldNotBeNull(array, "array");
        for (T item : array)
            if (item.equals(target))
                return true;
        return false;
    }

    /**
     * 배열에 지정한 항목의 위치 (0부터 시작)를 반환합니다. 같은 항목이 없으면 -1을 반환합니다.
     *
     * @param array  배열
     * @param target 찾을 항목
     * @return 찾을 항목의 위치 (0부터 시작). 없으면 -1 반환
     */
    public static <T> int indexOf(final T[] array, final T target) {
        Guard.shouldNotBeNull(array, "array");
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(target))
                return i;
        }
        return -1;
    }

    /**
     * 배열에서 지정한 항목의 위치를 뒤에서부터 찾습니다. 없으면 -1 반환.
     *
     * @param array  배열
     * @param target 찾을 항목
     * @param <T>    항목의 수형
     * @return 항목의 위치, 없으면 -1 반환
     */
    public static <T> int lastIndexOf(final T[] array, final T target) {
        Guard.shouldNotBeNull(array, "array");
        for (int i = array.length - 1; i >= 0; i--) {
            if (array[i].equals(target))
                return i;
        }
        return -1;
    }


    /**
     * 지정한 컬렉션을 배열로 변환합니다.
     *
     * @param collection the collections
     * @return the t [ ]
     * @see #asArray(Collection, Class) 를 사용하세요
     */
    @Deprecated
    public static <T> T[] asArray(final Collection<T> collection) {
        Guard.shouldNotBeNull(collection, "collections");

        T[] result = (T[]) java.lang.reflect.Array.newInstance(JavaReflects.getGenericParameterType(collection), collection.size());
        return collection.toArray(result);
    }

    /**
     * 지정한 컬렉션을 배열로 변환합니다.
     *
     * @param collection    컬렉션
     * @param componentType 배열 항목의 수형
     * @return 배열
     */
    public static <T> T[] asArray(final Collection<T> collection, final Class componentType) {
        Guard.shouldNotBeNull(collection, "collections");

        T[] result = (T[]) java.lang.reflect.Array.newInstance(componentType, collection.size());
        return collection.toArray(result);
    }

    /**
     * 컬렉션을 문자열로 변환합니다. 컬렉션의 내용을 보기 위해 사용합니다.
     *
     * @param iterable 컬렉션
     * @return 컬렉션을 표현하는 문자열
     */
    public static <T> String asString(final Iterable<? extends T> iterable) {
        return Strings.join(iterable);
    }

    /**
     * 지정한 크기의 난수 바이트 배열을 빌드합니다.
     *
     * @param size 배열의 크기
     * @return 난수로 채워진 바이트 배열
     */
    public static byte[] getRandomBytes(final int size) {
        return Cryptos.randomBytes(size);
    }

    /**
     * 배열을 리스트로 변환합니다.
     *
     * @param array 배열
     * @return 컬렉션
     */
    public static <T> List<T> toList(final Object[] array) {
        List<T> results = Lists.newArrayList();
        for (Object item : array) {
            results.add((T) item);
        }
        return results;
    }

    /**
     * 컬렉션을 특정 수형의 리스트로 변환합니다.
     *
     * @param iterable 원본 컬렉션
     * @return the list
     */
    public static <T> List<T> toList(final Iterable<?> iterable) {
        List<T> results = Lists.newArrayList();
        for (Object item : iterable) {
            results.add((T) item);
        }
        return results;
    }

    /**
     * 배열을 HashSet으로 변환합니다.
     *
     * @param array the array
     * @return the set
     */
    public static <T> Set<T> toSet(final Object[] array) {
        Set<T> results = Sets.newHashSet();
        for (Object item : array) {
            results.add((T) item);
        }
        return results;
    }

    /**
     * Iterable을 HashSet으로 변환합니다.
     *
     * @param iterable 원본 컬렉션
     * @return HashSet 인스턴스
     */
    public static <T> Set<T> toSet(final Iterable<?> iterable) {
        Set<T> results = Sets.newHashSet();
        for (Object item : iterable) {
            results.add((T) item);
        }
        return results;
    }
}
