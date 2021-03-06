package debop4s.core.parallels;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import debop4s.core.JAction1;
import debop4s.core.JFunction1;
import debop4s.core.collections.NumberRange;
import debop4s.core.concurrent.JAsyncs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import static debop4s.core.Guard.shouldNotBeNull;

/**
 * JParallels
 *
 * @author sunghyouk.bae@gmail.com
 */
public final class JParallels {

    private JParallels() { }

    private static final Logger log = LoggerFactory.getLogger(JParallels.class);

    public static final ThreadLocalRandom random = ThreadLocalRandom.current();
    public static final int processCount = Runtime.getRuntime().availableProcessors();
    public static final int workerCount = processCount * 2;

    /**
     * Fixed Thread Pool ({@link java.util.concurrent.ExecutorService}) 을 생성합니다.
     *
     * @return fixed thread pool
     */
    public static ExecutorService createExecutorService() {
        return createExecutorService(workerCount);
    }

    /**
     * 지정한 Worker Thread 갯수를 가지는 Fixed Thread Pool ({@link java.util.concurrent.ExecutorService}) 을 생성합니다.
     *
     * @param workerThreadCount Worker Thread 수
     * @return {@link java.util.concurrent.ExecutorService} 인스턴스
     */
    public static ExecutorService createExecutorService(final int workerThreadCount) {
        return Executors.newFixedThreadPool(workerThreadCount);
    }

    /**
     * 컬렉션을 분할하게 될 때, 분할된 파티션의 항목 수
     *
     * @param itemCount      항목의 전체 갯수
     * @param partitionCount 분할할 갯수
     * @return 한 파티션의 항목 수
     */
    private static int getPartitionSize(final int itemCount, final int partitionCount) {
        return (itemCount / partitionCount) + ((itemCount % partitionCount) > 0 ? 1 : 0);
    }

    /**
     * 지정한 작업을 병렬로 수행합니다.

     * @param count the count
     * @param runnable the runnable
     */
    public static void run(final int count,
                           final Runnable runnable) {
        run(0, count, runnable);
    }

    /**
     * runnable 을 병렬로 실행하고, 완료될 때까지 대기합니다.
     *
     * @param fromInclude the from include
     * @param toExclude   the to exclude
     * @param action      the action
     */
    public static void run(final int fromInclude,
                           final int toExclude,
                           final Runnable action) {
        int step = (fromInclude <= toExclude) ? 1 : -1;
        run(fromInclude, toExclude, step, action);
    }

    /**
     * runnable 을 병렬로 실행하고, 완료될 때까지 대기합니다.
     *
     * @param fromInclude the from include
     * @param toExclude   the to exclude
     * @param step        the step
     * @param runnable    the runnable
     */
    public static void run(final int fromInclude,
                           final int toExclude,
                           final int step,
                           final Runnable runnable) {
        shouldNotBeNull(runnable, "runnable");

        ExecutorService executor = createExecutorService();

        try {
            List<NumberRange.IntRange> partitions = NumberRange.partition(fromInclude, toExclude, step, workerCount);
            List<Callable<Void>> tasks = Lists.newLinkedList();

            for (final NumberRange.IntRange partition : partitions) {
                Callable<Void> task =
                        new Callable<Void>() {
                            @Override
                            public Void call() throws Exception {
                                for (final int ignored : partition)
                                    runnable.run();
                                return null;
                            }
                        };
                tasks.add(task);
            }

            List<Future<Void>> results = executor.invokeAll(tasks);

            JAsyncs.waitAll(results);
            log.debug("병렬로 작업을 수행합니다... fromInclude=[{}], toExclude=[{}], step=[{}], workerCount=[{}]",
                      fromInclude, toExclude, step, workerCount);

        } catch (Exception e) {
            log.error("데이터에 대한 병렬 작업 중 예외가 발생했습니다.", e);
            throw new RuntimeException(e);
        } finally {
            executor.shutdown();
        }
    }

    /**
     * action을 병렬로 실행하고, 완료될 때까지 기다립니다.
     *
     * @param count  the count
     * @param action the action
     */
    public static void run(final int count,
                           final JAction1<Integer> action) {
        run(0, count, action);
    }

    /**
     * action을 병렬로 실행하고, 완료될 때까지 기다립니다.
     *
     * @param fromInclude the from include
     * @param toExclude   the to exclude
     * @param action      the action
     */
    public static void run(final int fromInclude,
                           final int toExclude,
                           final JAction1<Integer> action) {
        int step = (fromInclude <= toExclude) ? 1 : -1;
        run(fromInclude, toExclude, step, action);
    }

    /**
     * action을 병렬로 실행하고, 완료될 때까지 기다립니다.
     *
     * @param fromInclude the from include
     * @param toExclude   the to exclude
     * @param step        the step
     * @param action      the action
     */
    public static void run(final int fromInclude,
                           final int toExclude,
                           final int step,
                           final JAction1<Integer> action) {
        shouldNotBeNull(action, "action");
        log.trace("병렬로 작업을 수행합니다... fromInclude=[{}], toExclude=[{}], step=[{}], workerCount=[{}]",
                  fromInclude, toExclude, step, workerCount);

        ExecutorService executor = createExecutorService();

        try {
            List<NumberRange.IntRange> partitions = NumberRange.partition(fromInclude, toExclude, step, workerCount);
            List<Callable<Void>> tasks = Lists.newLinkedList();

            for (final NumberRange.IntRange partition : partitions) {
                Callable<Void> task =
                        new Callable<Void>() {
                            @Override
                            public Void call() throws Exception {
                                for (final int element : partition)
                                    action.perform(element);
                                return null;
                            }
                        };
                tasks.add(task);
            }

            List<Future<Void>> results = executor.invokeAll(tasks);
            JAsyncs.waitAll(results);
            log.debug("모든 작업을 병렬로 수행하였습니다!");

        } catch (Exception e) {
            log.error("데이터에 대한 병렬 작업 중 예외가 발생했습니다.", e);
            throw new RuntimeException(e);
        } finally {
            executor.shutdown();
        }
    }

    /**
     * {@link java.util.concurrent.Callable} 을 병렬로 수행하고, 결과를 반환합니다. (순서에는 상관없습니다)
     *
     * @param count    the count
     * @param callable the func
     * @return the list
     */
    public static <V> List<V> run(final int count,
                                  final Callable<V> callable) {
        return run(0, count, callable);
    }

    /**
     * {@link java.util.concurrent.Callable} 을 병렬로 수행하고, 결과를 반환합니다. (순서에는 상관없습니다)
     *
     * @param fromInclude the from include
     * @param toExclude   the to exclude
     * @param callable    the func
     * @return the list
     */
    public static <V> List<V> run(final int fromInclude,
                                  final int toExclude,
                                  final Callable<V> callable) {
        int step = (fromInclude <= toExclude) ? 1 : -1;
        return run(fromInclude, toExclude, step, callable);
    }

    /**
     * {@link java.util.concurrent.Callable} 을 병렬로 수행하고, 결과를 반환합니다. (순서에는 상관없습니다)
     *
     * @param fromInclude the from include
     * @param toExclude   the to exclude
     * @param step        the step
     * @param callable    the func
     * @return the list
     */
    public static <V> List<V> run(final int fromInclude,
                                  final int toExclude,
                                  final int step,
                                  final Callable<V> callable) {
        shouldNotBeNull(callable, "func");
        log.trace("병렬로 작업을 수행합니다... fromInclude=[{}], toExclude=[{}], step=[{}], workerCount=[{}]",
                  fromInclude, toExclude, step, workerCount);

        ExecutorService executor = createExecutorService();

        try {
            List<NumberRange.IntRange> partitions = NumberRange.partition(fromInclude, toExclude, step, workerCount);
            final Map<Integer, List<V>> localResults = new LinkedHashMap<>();
            List<Callable<List<V>>> tasks = Lists.newLinkedList(); // False Sharing을 방지하기 위해

            for (int p = 0; p < partitions.size(); p++) {
                final NumberRange.IntRange partition = partitions.get(p);
                final List<V> localResult = Lists.newArrayListWithCapacity(partition.size());
                localResults.put(p, localResult);

                Callable<List<V>> task = new Callable<List<V>>() {
                    @Override
                    public List<V> call() throws Exception {
                        for (final int ignored : partition)
                            localResult.add(callable.call());
                        return localResult;
                    }
                };
                tasks.add(task);
            }

            executor.invokeAll(tasks);

            List<V> results = new CopyOnWriteArrayList<>();
            for (int i = 0; i < partitions.size(); i++) {
                results.addAll(localResults.get(i));
            }

            log.debug("모든 작업을 병렬로 완료했습니다. workerCount=[{}]", workerCount);

            return results;
        } catch (Exception e) {
            log.error("데이터에 대한 병렬 작업 중 예외가 발생했습니다.", e);
            throw new RuntimeException(e);
        } finally {
            executor.shutdown();
        }
    }

    /**
     * Run list.
     *
     * @param count    the count
     * @param function the function
     * @return the list
     */
    public static <V> List<V> run(final int count,
                                  final JFunction1<Integer, V> function) {
        return run(0, count, function);
    }

    /**
     * 지정한 범위의 정보를 수행합니다.
     *
     * @param fromInclude 시작 인덱스 (하한)
     * @param toExclude   종료 인덱스 (상한)
     * @param function    the function
     * @return 결과 값 컬렉션
     */
    public static <V> List<V> run(final int fromInclude,
                                  final int toExclude,
                                  final JFunction1<Integer, V> function) {
        int step = (fromInclude <= toExclude) ? 1 : -1;
        return run(fromInclude, toExclude, step, function);
    }

    /**
     * 지정한 범위의 정보를 수행합니다.
     *
     * @param fromInclude 시작 인덱스 (하한)
     * @param toExclude   종료 인덱스 (상한)
     * @param step        Step
     * @param function    수행할 함수
     * @return 결과 값 컬렉션
     */
    public static <V> List<V> run(final int fromInclude,
                                  final int toExclude,
                                  final int step,
                                  final JFunction1<Integer, V> function) {
        shouldNotBeNull(function, "function");

        ExecutorService executor = createExecutorService();
        try {
            List<NumberRange.IntRange> partitions = NumberRange.partition(fromInclude, toExclude, step, workerCount);
            final Map<Integer, List<V>> localResults = Maps.newLinkedHashMap();
            List<Callable<List<V>>> tasks = Lists.newLinkedList(); // False Sharing을 방지하기 위해

            for (int p = 0; p < partitions.size(); p++) {
                final NumberRange.IntRange partition = partitions.get(p);
                final List<V> localResult = Lists.newArrayListWithCapacity(partition.size());
                localResults.put(p, localResult);
                Callable<List<V>> task = new Callable<List<V>>() {
                    @Override
                    public List<V> call() throws Exception {
                        for (final int element : partition)
                            localResult.add(function.execute(element));
                        return localResult;
                    }
                };
                tasks.add(task);
            }

            executor.invokeAll(tasks);

            List<V> results = Lists.newArrayListWithCapacity(partitions.size());
            for (int i = 0; i < partitions.size(); i++) {
                results.addAll(localResults.get(i));
            }
            log.debug("모든 작업을 병렬로 완료했습니다. workerCount=[{}]", workerCount);

            return results;
        } catch (Exception e) {
            log.error("데이터에 대한 병렬 작업 중 예외가 발생했습니다.", e);
            throw new RuntimeException(e);
        } finally {
            executor.shutdown();
        }
    }

    /**
     * 지정한 컬렉션을 분할하여, 멀티스레드 환경하에서 작업을 수행합니다.
     *
     * @param elements action을 입력 인자로 사용할 컬렉션
     * @param action   수행할 function
     */
    public static <T> void runEach(final Iterable<T> elements,
                                   final JAction1<T> action) {
        shouldNotBeNull(elements, "elements");
        shouldNotBeNull(action, "function");
        log.trace("병렬로 작업을 수행합니다... workerCount=[{}]", workerCount);

        ExecutorService executor = createExecutorService();

        try {
            List<T> elemList = Lists.newArrayList(elements);
            int partitionSize = getPartitionSize(elemList.size(), workerCount);
            Iterable<List<T>> partitions = Iterables.partition(elemList, partitionSize);
            List<Callable<Void>> tasks = Lists.newLinkedList();

            for (final List<T> partition : partitions) {
                Callable<Void> task =
                        new Callable<Void>() {
                            @Override
                            public Void call() throws Exception {
                                for (final T element : partition)
                                    action.perform(element);
                                return null;
                            }
                        };
                tasks.add(task);
            }
            List<Future<Void>> results = executor.invokeAll(tasks);
            JAsyncs.waitAll(results);
            log.debug("모든 작업을 병렬로 수행하였습니다. workerCount=[{}]", workerCount);

        } catch (Exception e) {
            log.error("데이터에 대한 병렬 작업 중 예외가 발생했습니다.", e);
            throw new RuntimeException(e);
        } finally {
            executor.shutdown();
        }
    }

    /**
     * 지정한 컬렉션을 분할해서, 병렬로 function을 수행하고, 결과를 반환합니다.
     *
     * @param elements function의 입력 정보
     * @param function 수행할 함수
     * @return 수행 결과의 컬렉션
     */
    public static <T, V> List<V> runEach(final Iterable<T> elements,
                                         final JFunction1<T, V> function) {
        shouldNotBeNull(elements, "elements");
        shouldNotBeNull(function, "function");
        log.trace("병렬로 작업을 수행합니다... workerCount=[{}]", workerCount);

        ExecutorService executor = createExecutorService();

        try {
            List<T> elemList = Lists.newArrayList(elements);
            int partitionSize = getPartitionSize(elemList.size(), workerCount);
            List<List<T>> partitions = Lists.partition(elemList, partitionSize);
            final Map<Integer, List<V>> localResults = Maps.newLinkedHashMap();
            List<Callable<List<V>>> tasks = Lists.newLinkedList(); // False Sharing을 방지하기 위해

            for (int p = 0; p < partitions.size(); p++) {
                final List<T> partition = partitions.get(p);
                final List<V> localResult = Lists.newArrayListWithCapacity(partition.size());
                localResults.put(p, localResult);

                Callable<List<V>> task = new Callable<List<V>>() {
                    @Override
                    public List<V> call() throws Exception {
                        for (final T element : partition)
                            localResult.add(function.execute(element));
                        return localResult;
                    }
                };
                tasks.add(task);
            }

            executor.invokeAll(tasks);

            List<V> results = Lists.newArrayListWithCapacity(elemList.size());

            for (int i = 0; i < partitions.size(); i++) {
                results.addAll(localResults.get(i));
            }
            log.debug("모든 작업을 병렬로 완료했습니다. workerCount=[{}]", workerCount);
            return results;

        } catch (Exception e) {
            log.error("데이터에 대한 병렬 작업 중 예외가 발생했습니다.", e);
            throw new RuntimeException(e);
        } finally {
            executor.shutdown();
        }
    }

    /**
     * 지정한 범위의 정보를 수행합니다.
     *
     * @param count  수행할 횟수
     * @param action 수행할 함수
     */
    public static void runPartitions(final int count,
                                     final JAction1<List<Integer>> action) {
        runPartitions(0, count, action);
    }

    /**
     * 지정한 범위의 정보를 수행합니다.
     *
     * @param fromInclude 시작 인덱스 (하한)
     * @param toExclude   종료 인덱스 (상한)
     * @param action      수행할 함수
     */
    public static void runPartitions(final int fromInclude,
                                     final int toExclude,
                                     final JAction1<List<Integer>> action) {
        int step = (fromInclude <= toExclude) ? 1 : -1;
        runPartitions(fromInclude, toExclude, step, action);
    }

    /**
     * 지정한 범위의 정보를 수행합니다.
     *
     * @param fromInclude 시작 인덱스 (하한)
     * @param toExclude   종료 인덱스 (상한)
     * @param step        Step
     * @param action      수행할 함수
     */
    public static void runPartitions(final int fromInclude,
                                     final int toExclude,
                                     final int step,
                                     final JAction1<List<Integer>> action) {
        shouldNotBeNull(action, "action");

        ExecutorService executor = createExecutorService();

        try {
            List<NumberRange.IntRange> partitions = NumberRange.partition(fromInclude, toExclude, step, workerCount);
            List<Callable<Void>> tasks = Lists.newLinkedList();

            for (NumberRange.IntRange partition : partitions) {
                final List<Integer> inputs = Lists.newArrayList(partition.iterator());
                Callable<Void> task = new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        action.perform(inputs);
                        return null;
                    }
                };
                tasks.add(task);
            }
            List<Future<Void>> results = executor.invokeAll(tasks);
            JAsyncs.waitAll(results);
            log.debug("모든 작업을 병렬로 수행하였습니다!");
        } catch (Exception e) {
            log.error("데이터에 대한 병렬 작업 중 예외가 발생했습니다.", e);
            throw new RuntimeException(e);
        } finally {
            executor.shutdown();
        }
    }

    /**
     * 지정한 범위의 정보를 수행합니다.
     *
     * @param count    수행할 횟수
     * @param function 수행할 함수
     * @return 결과 값 컬렉션
     */
    public static <V> List<V> runPartitions(final int count,
                                            final JFunction1<List<Integer>, List<V>> function) {
        return runPartitions(0, count, function);
    }

    /**
     * 지정한 범위의 정보를 수행합니다.
     *
     * @param fromInclude 시작 인덱스 (하한)
     * @param toExclude   종료 인덱스 (상한)
     * @param function    수행할 함수
     * @return 결과 값 컬렉션
     */
    public static <V> List<V> runPartitions(int fromInclude,
                                            int toExclude,
                                            final JFunction1<List<Integer>, List<V>> function) {
        int step = (fromInclude <= toExclude) ? 1 : -1;
        return runPartitions(fromInclude, toExclude, step, function);
    }

    /**
     * 지정한 범위의 정보를 수행합니다.
     *
     * @param fromInclude 시작 인덱스 (하한)
     * @param toExclude   종료 인덱스 (상한)
     * @param step        Step
     * @param function    수행할 함수
     * @return 결과 값 컬렉션
     */
    public static <V> List<V> runPartitions(int fromInclude,
                                            int toExclude,
                                            int step,
                                            final JFunction1<List<Integer>, List<V>> function) {
        shouldNotBeNull(function, "function");

        ExecutorService executor = createExecutorService();
        try {
            List<NumberRange.IntRange> partitions = NumberRange.partition(fromInclude, toExclude, step, workerCount);
            List<Callable<List<V>>> tasks = Lists.newLinkedList(); // False Sharing을 방지하기 위해

            for (final NumberRange.IntRange partition : partitions) {
                final List<Integer> inputs = Lists.newArrayList(partition.iterator());
                Callable<List<V>> task = new Callable<List<V>>() {
                    @Override
                    public List<V> call() throws Exception {
                        return function.execute(inputs);
                    }
                };
                tasks.add(task);
            }
            // 작업 시작
            List<Future<List<V>>> outputs = executor.invokeAll(tasks);

            List<V> results = Lists.newArrayList();
            for (Future<List<V>> output : outputs) {
                results.addAll(output.get());
            }
            log.debug("모든 작업을 병렬로 완료했습니다. workerCount=[{}]", workerCount);
            return results;
        } catch (Exception e) {
            log.error("데이터에 대한 병렬 작업 중 예외가 발생했습니다.", e);
            throw new RuntimeException(e);
        } finally {
            executor.shutdown();
        }
    }

    /**
     * 지정한 컬렉션을 분할하여, 병렬로 작업을 수행합니다.
     *
     * @param elements 처리할 데이터
     * @param action   수행할 코드
     */
    public static <T> void runPartitions(final Iterable<T> elements,
                                         final JAction1<List<T>> action) {
        shouldNotBeNull(elements, "elements");
        shouldNotBeNull(action, "function");
        log.trace("병렬로 작업을 수행합니다... workerCount=[{}]", workerCount);

        ExecutorService executor = createExecutorService();

        try {
            List<T> elemList = Lists.newArrayList(elements);
            int partitionSize = getPartitionSize(elemList.size(), workerCount);
            Iterable<List<T>> partitions = Iterables.partition(elemList, partitionSize);
            List<Callable<Void>> tasks = Lists.newLinkedList();

            for (final List<T> partition : partitions) {
                Callable<Void> task = new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        action.perform(partition);
                        return null;
                    }
                };
                tasks.add(task);
            }
            // 작업 시작
            List<Future<Void>> results = executor.invokeAll(tasks);
            JAsyncs.waitAll(results);
            log.debug("모든 작업을 병렬로 수행했습니다. workCount=[{}]", workerCount);
        } catch (Exception e) {
            log.error("데이터에 대한 병렬작업중 예외가 발생했습니다.", e);
            throw new RuntimeException(e);
        } finally {
            executor.shutdown();
        }
    }

    /**
     * 지정한 컬렉션을 분할하여, 병렬로 작업을 수행합니다.
     *
     * @param elements 처리할 데이터
     * @param function 수행할 코드
     * @return 수행한 결과
     */
    public static <T, V> List<V> runPartitions(final Iterable<T> elements,
                                               final JFunction1<List<T>, List<V>> function) {
        shouldNotBeNull(elements, "elements");
        shouldNotBeNull(function, "function");
        log.trace("병렬로 작업을 수행합니다... workerCount=[{}]", workerCount);

        ExecutorService executor = createExecutorService();

        try {
            List<T> elemList = Lists.newArrayList(elements);
            int partitionSize = getPartitionSize(elemList.size(), workerCount);
            List<List<T>> partitions = Lists.partition(elemList, partitionSize);
            //final Map<Integer, List<V>> localResults = new LinkedHashMap<>();

            List<Callable<List<V>>> tasks = Lists.newLinkedList(); // False Sharing을 방지하기 위해

            for (final List<T> partition : partitions) {
                Callable<List<V>> task = new Callable<List<V>>() {
                    @Override
                    public List<V> call() throws Exception {
                        return function.execute(partition);
                    }
                };
                tasks.add(task);
            }
            // 작업 시작
            List<Future<List<V>>> futures = executor.invokeAll(tasks);

            List<V> results = Lists.newArrayListWithCapacity(elemList.size());
            for (Future<List<V>> future : futures)
                results.addAll(future.get());

            log.debug("모든 작업을 병렬로 완료했습니다. workerCount=[{}]", workerCount);
            return results;

        } catch (Exception e) {
            log.error("데이터에 대한 병렬 작업 중 예외가 발생했습니다.", e);
            throw new RuntimeException(e);
        } finally {
            executor.shutdown();
        }
    }
}
