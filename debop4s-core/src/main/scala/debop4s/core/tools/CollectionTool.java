package debop4s.core.tools;

//import jodd.util.Tuple2;

import jodd.util.collection.SortedArrayList;
import scala.Tuple2;

import java.util.*;

/**
 * Collection 관련 Method를 제공하는 Helper Class 입니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 12. 13. 오후 6:39
 */
public final class CollectionTool {

    /**
     * 맵의 Key 로 순차 정렬을 수행합니다.
     *
     * @param map 원본 맵
     * @return 맵의 키로 순차 정렬된 맵
     */
    public static <K extends Comparable<? super K>, V> Map<K, V> sortByKey(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o1.getKey()).compareTo(o2.getKey());
            }
        });
        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    /**
     * 맵의 Key 로 역순 정렬을 수행합니다.
     *
     * @param map 원본 맵
     * @return 맵의 키로 역순 정렬된 맵
     */
    public static <K extends Comparable<? super K>, V> Map<K, V> sortByKeyDescending(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o2.getKey()).compareTo(o1.getKey());
            }
        });
        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    /**
     * 맵의 Value 로 순차 정렬을 수행합니다.
     *
     * @param map 원본 맵
     * @return 맵의 Value로 순차 정렬된 맵
     */
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    /**
     * 맵의 Value 로 역순 정렬을 수행합니다.
     *
     * @param map 원본 맵
     * @return 맵의 Value로 역순 정렬된 맵
     */
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValueDescending(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    /**
     * 맵을 comparator 를 이용하여 정렬합니다.
     *
     * @param map        정렬할 Map
     * @param comparator 정렬할 비교자
     * @param <K>        Key 수형
     * @param <V>        Value 수형
     * @return 정렬된 맵
     */
    public static <K extends Comparable<? super K>, V extends Comparable<? super V>>
    List<Tuple2<K, V>> sortMapToList(Map<K, V> map, Comparator<Tuple2<K, V>> comparator) {
        List<Tuple2<K, V>> results = new SortedArrayList<>(comparator);

        for (Map.Entry<K, V> entry : map.entrySet())
            results.add(new Tuple2<>(entry.getKey(), entry.getValue()));

        return results;
    }

}
