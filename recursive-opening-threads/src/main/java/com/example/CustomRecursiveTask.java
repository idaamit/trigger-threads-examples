package com.example;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

@Slf4j
public class CustomRecursiveTask extends RecursiveTask<Integer> {
    private static final int THRESHOLD = 2;
    private int[] array;


    public CustomRecursiveTask(int[] array) {
        this.array = array;
    }

    @Override
    protected Integer compute() {
        int sum = 0;
        ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();
        log.debug("Task started [{}] ActiveThreadCount={}, RunningThreadCount={}, PoolSize={}, Parallelism={}", Arrays.toString(array) ,
                forkJoinPool.getActiveThreadCount(), forkJoinPool.getRunningThreadCount(), forkJoinPool.getPoolSize(), forkJoinPool.getParallelism());
        if (array.length > THRESHOLD) {
            sum = ForkJoinTask.invokeAll(createSubtasks(array))
                    .stream()
                    .mapToInt(
                            CustomRecursiveTask::join
//                    customRecursiveTask -> {
//                        try {
//                            return customRecursiveTask.get();    // a better way to user CustomRecursiveTask::join since it doesn't throw exception
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        } catch (ExecutionException e) {
//                            e.printStackTrace();
//                        }
//                        return 0;
                    )
                    .sum();
        } else {
            sum = processing(array);
        }
        log.debug("Task ended sum[{}]={} ActiveThreadCount={}, RunningThreadCount={}, PoolSize={}, Parallelism={}", Arrays.toString(array) , sum ,
                forkJoinPool.getActiveThreadCount(), forkJoinPool.getRunningThreadCount(), forkJoinPool.getPoolSize(), forkJoinPool.getParallelism());
        return sum;
    }

    private Collection<CustomRecursiveTask> createSubtasks(int[] array) {
        int[] arr1 = Arrays.copyOfRange(array, 0, array.length / 2);
        int[] arr2 = Arrays.copyOfRange(array, array.length / 2, array.length);
        List<CustomRecursiveTask> dividedTasks = new ArrayList<>();
        dividedTasks.add(new CustomRecursiveTask(arr1));
        dividedTasks.add(new CustomRecursiveTask(arr2));
        return dividedTasks;
    }

    private Integer processing(int[] array) {
        int result = Arrays.stream(array)
                .sum();
        System.out.println(Thread.currentThread().getName() + Arrays.toString(array) + " result - (" + result + ") - was processed.");
        return result;
    }
}
