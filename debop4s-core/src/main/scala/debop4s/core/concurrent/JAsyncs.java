package debop4s.core.concurrent;

import debop4s.core.JFunction1;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

/**
 * JAsyncs
 *
 * @author sunghyouk.bae@gmail.com
 */
@Slf4j
public class JAsyncs {

    public static ExecutorService executorService =
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);

    /** Empty Runnable */
    public static Runnable EMPTY_RUNNABLE = new Runnable() {
        @Override
        public void run() {
            // Nothing to do.
        }
    };

    /**
     * 새로운 비동기 Task를 생성합니다. 작업 수행을 시작하지는 않습니다.
     *
     * @param callable 수행할 callable 객체
     * @param <T>      반환 값의 수형
     * @return {@link java.util.concurrent.FutureTask} 인스턴스
     */
    public static <T> FutureTask<T> newTask(Callable<T> callable) {
        return new FutureTask<>(callable);
    }

    /**
     * 새로운 비동기 Task를 생성합니다. 작업 수행을 시작하지는 않습니다.
     *
     * @param runnable 수행할 runnable 객체
     * @param result   수행 결과 값
     * @param <T>      반환 값의 수형
     * @return {@link java.util.concurrent.FutureTask} 인스턴스
     */
    public static <T> FutureTask<T> newTask(Runnable runnable, T result) {
        return new FutureTask<>(runnable, result);
    }

    /**
     * 새로운 비동기 Task를 생성합니다. 작업 수행을 시작하지는 않습니다.
     *
     * @param runnable 수행할 runnable 객체
     * @return {@link java.util.concurrent.FutureTask} 인스턴스
     */
    public static FutureTask<Void> newTask(Runnable runnable) {
        return newTask(runnable, null);
    }

    /**
     * 비동기 방식으로 작업을 시작합니다.
     *
     * @param task 작엽
     * @param <V>  작업 결과 수형
     * @return {@link java.util.concurrent.Future} 인스턴스
     */
    public static <V> Future<V> startNew(Callable<V> task) {
        return executorService.submit(task);
    }

    /**
     * 새로운 비동기 Task를 실행합니다.
     *
     * @param task   수행할 runnable 객체
     * @param result 수행 결과 값
     * @param <V>    반환 값의 수형
     * @return {@link java.util.concurrent.Future} 인스턴스
     */
    public static <V> Future<V> startNew(Runnable task, V result) {
        return executorService.submit(task, result);
    }

    /**
     * prevTask 작업이 완료되길 기다린 후, 결과값을 func에 입력하여 실행시킵니다.
     * 메소드 chain 을 만들 수 있습니다.
     *
     * @param prevTask 선행 작업
     * @param func     후행 작업
     * @param <V>      선행 작업 결과 값의 수형
     * @param <R>      후행 작업 결과 값의 수형
     * @return 후행 작업 결과를 가지는 {@link java.util.concurrent.Future}
     */
    public static <V, R> Future<R> continueTask(final FutureTask<V> prevTask, final JFunction1<V, R> func) {
        return startNew(new Callable<R>() {
            @Override
            public R call() throws Exception {
                final V pv = prevTask.get();
                return func.execute(pv);
            }
        });
    }

    /**
     * 지정한 결과를 가지는 {@link java.util.concurrent.FutureTask} 를 반환합니다.
     *
     * @param result FutureTask의 결과 값
     * @param <T>    결과 값의 수형
     * @return {@link java.util.concurrent.FutureTask} 인스턴스
     */
    public static <T> FutureTask<T> getTaskHasResult(T result) {
        return newTask(EMPTY_RUNNABLE, result);
    }

    /**
     * 비동기 방식으로 컬렉션의 항목을 인자로 func을 수행하고 결과를 반환한다.
     *
     * @param elements 인자값 컬렉션
     * @param func     수행할 함수 객체
     * @param <T>      입력 인자의 수형
     * @param <R>      수행 결과의 수형
     * @return 수행 결과의 {@link java.util.concurrent.Future}의 컬렉션
     */
    public static <T, R> List<Future<R>> runAsync(final Iterable<T> elements, final JFunction1<T, R> func) {
        final List<Callable<R>> tasks = new ArrayList<>();

        for (final T elem : elements) {
            final Callable<R> task = new Callable<R>() {
                @Override
                public R call() throws Exception {
                    return func.execute(elem);
                }
            };
            tasks.add(task);
        }

        try {
            return executorService.invokeAll(tasks);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 모든 태스트를 실행하고, 완료될 때까지 기다립니다.
     *
     * @param tasks 작업
     * @param <T>   반환될 수형
     * @return 작업 결과 컬렉션
     */
    public static <T> List<T> invokeAll(Collection<? extends Callable<T>> tasks) {
        try {
            return getAll(executorService.invokeAll(tasks));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 지정한 Task들을 모두 수행하도록합니다. 제한된 시간이 경과하면 예외를 발생시킵니다.
     *
     * @param tasks   수행할 작업들
     * @param timeout 제한 시간
     * @param unit    제한 시간의 단위
     * @param <T>     반환 값의 수형
     * @return 작업 결과 컬렉션
     */
    public static <T> List<T> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) {
        try {
            return getAll(executorService.invokeAll(tasks, timeout, unit));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 지정한 모든 Future 들이 완료되기를 기다립니다.
     *
     * @param futures Future 배열
     */
    @SafeVarargs
    public static <T> void runAll(Future<T>... futures) {
        try {
            getAll(futures);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 지정한 모든 Future 들이 완료되기를 기다립니다.
     *
     * @param futures Future 배열
     */
    public static void runAll(Iterable<? extends Future<Object>> futures) {
        try {
            getAll(futures);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 모든 작업이 결과를 반환합니다.
     *
     * @param futures 작업 배열
     * @return 작업 결과 컬렉션
     * @throws java.util.concurrent.ExecutionException
     * @throws InterruptedException
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> getAll(Future<T>... futures) throws ExecutionException, InterruptedException {
        List<T> results = new ArrayList<>();
        for (Future<T> future : futures) {
            results.add(future.get());
        }
        return results;
    }

    /**
     * 모든 작업이 결과를 반환합니다.
     *
     * @param futures 작업 배열
     * @return 작업 결과 컬렉션
     * @throws java.util.concurrent.ExecutionException
     * @throws InterruptedException
     */
    public static <T> List<T> getAll(Iterable<? extends Future<T>> futures) throws ExecutionException, InterruptedException {
        List<T> results = new ArrayList<>();
        for (final Future<T> task : futures) {
            results.add(task.get());
        }
        return results;
    }

    /**
     * 모든 작업이 결과를 반환합니다.
     *
     * @param futures 작업 배열
     * @param timeout 타임아웃 값
     * @param unit    타임아웃 단위
     * @return 작업 결과 컬렉션
     * @throws java.util.concurrent.ExecutionException
     * @throws InterruptedException
     */
    public static <T> List<T> getAll(Iterable<? extends Future<T>> futures, long timeout, TimeUnit unit)
            throws ExecutionException, InterruptedException, TimeoutException {
        List<T> results = new ArrayList<>();
        for (final Future<T> task : futures) {
            results.add(task.get(timeout, unit));
        }
        return results;
    }

    /**
     * 모든 작업이 완료될 때까지 기다립니다.
     *
     * @param futures {@link java.util.concurrent.Future} 컬렉션
     */
    public static void waitAll(Future<?>... futures) {
        try {
            Thread.sleep(1);
            for (Future<?> future : futures) {
                future.get();
                Thread.sleep(1);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 모든 작업이 완료될 때까지 기다립니다.
     *
     * @param futures {@link java.util.concurrent.Future} 컬렉션
     */
    public static <T> void waitAll(Iterable<Future<T>> futures) {
        try {
            Thread.sleep(1);
            for (Future<?> future : futures) {
                future.get();
                Thread.sleep(1);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 모든 작업이 완료될 때까지 기다립니다.
     *
     * @param tasks {@link java.util.concurrent.FutureTask} 배열
     */
    public static void waitAllTasks(FutureTask<?>... tasks) {
        try {
            Thread.sleep(1);
            for (FutureTask<?> task : tasks) {
                task.get();
                Thread.sleep(1);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 모든 작업이 완료될 때까지 기다립니다.
     *
     * @param tasks {@link java.util.concurrent.FutureTask} 배열
     */
    public static <T> void waitAllTasks(Iterable<FutureTask<T>> tasks) {
        try {
            Thread.sleep(1);
            for (FutureTask<?> task : tasks) {
                task.get();
                Thread.sleep(1);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
