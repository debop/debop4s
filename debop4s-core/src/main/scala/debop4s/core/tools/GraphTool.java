package debop4s.core.tools;

import debop4s.core.JFunction1;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * Adjacent Graph 를 탐색하는 메소드를 제공합니다.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 8. 오후 8:24
 */
@Slf4j
public final class GraphTool {

    private GraphTool() { }

    /**
     * 폭 우선 탐색을 수행하여, 탐색한 항목들을 컬렉션으로 반환합니다.
     *
     * @param source      시작 노드
     * @param getAdjacent 근접한 노드들을 반환하는 함수
     * @param <T>         노드의 수형
     * @return 폭 우선 탐색으로 찾은 노드들
     */
    public static <T> List<T> graphBreadthFirstScan(T source, JFunction1<T, Iterable<T>> getAdjacent) {
        assert (source != null);
        assert (getAdjacent != null);

        Queue<T> toScan = new ArrayDeque<>();
        Set<T> scanned = new HashSet<>();

        toScan.add(source);
        while (toScan.size() > 0) {
            T current = toScan.peek();
            scanned.add(current);
            toScan.remove(current);

            for (T node : getAdjacent.execute(current)) {
                if (!scanned.contains(node)) {
                    toScan.add(node);
                }
            }
        }
        return new ArrayList<>(scanned);
    }

    /**
     * 깊이 우선 탐색을 수행하고, 탐색한 노드들을 반환합니다.
     *
     * @param source      시작 노드
     * @param getAdjacent 근접한 노드들을 반환하는 함수
     * @param <T>         노드의 수형
     * @return 폭 우선 탐색으로 찾은 노드들
     */
    public static <T> List<T> graphDepthFirstScan(T source, JFunction1<T, Iterable<T>> getAdjacent) {
        assert (source != null);
        assert (getAdjacent != null);

        Stack<T> toScan = new Stack<>();
        Set<T> scanned = new HashSet<>();

        toScan.add(source);
        while (toScan.size() > 0) {
            T current = toScan.pop();
            scanned.add(current);
            for (T node : getAdjacent.execute(current)) {
                if (!scanned.contains(node)) {
                    toScan.push(node);
                }
            }
        }
        return new ArrayList<>(scanned);
    }
}
