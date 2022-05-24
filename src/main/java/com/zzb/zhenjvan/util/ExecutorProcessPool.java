package com.zzb.zhenjvan.util;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;


/**
 * @author lisai
 * @title: ExecutorProcessPool
 * @projectName aml
 * @description: 线程池工具类
 * @date 2020/8/28 10:52
 */
@Slf4j
@Data
@Component
public class ExecutorProcessPool {

    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    private static ExecutorProcessPool pool = new ExecutorProcessPool();

    @Bean
    public void init() {
        log.info("======================线程池实例化===============");
        threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(8);
        threadPoolTaskExecutor.setMaxPoolSize(8);
        threadPoolTaskExecutor.setQueueCapacity(1200);
        threadPoolTaskExecutor.setThreadNamePrefix("aml-thread-");
        // 任务执行
        threadPoolTaskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        /*
         *  使用此策略，如果添加到线程池失败，那么主线程会自己去执行该任务，不会等待线程池中的线程去执行
         */
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        threadPoolTaskExecutor.setKeepAliveSeconds(60);
        threadPoolTaskExecutor.initialize();
        pool.setThreadPoolTaskExecutor(threadPoolTaskExecutor);
    }

    public static ExecutorProcessPool getInstance() {
        return pool;
    }

    /**
     * 关闭线程池，这里要说明的是：调用关闭线程池方法后，线程池会执行完队列中的所有任务才退出
     */
    public void shutdown() {
        log.info("Thread shutdown========");
        threadPoolTaskExecutor.shutdown();
    }

    /**
     * 提交任务到线程池，可以接收线程返回值
     *
     * @param task
     * @return
     */
    public Future<?> submit(Runnable task) {
        return threadPoolTaskExecutor.submit(task);
    }

    /**
     * 提交任务到线程池，可以接收线程返回值
     *
     * @param task
     * @return
     */
    public <T> Future<T> submit(Callable<T> task) {
        return threadPoolTaskExecutor.submit(task);
    }

    /**
     * 直接提交任务到线程池，无返回值
     *
     * @param task
     */
    public void execute(Runnable task) {
        threadPoolTaskExecutor.execute(task);
    }

    /**
     * 输出当前线程池情况
     */
    public Map printThreadPoll() {
        Map<String, Object> map = new HashMap<>();
        map.put("提交任务数", threadPoolTaskExecutor.getThreadPoolExecutor().getTaskCount());
        map.put("队列中任务数", threadPoolTaskExecutor.getThreadPoolExecutor().getQueue().size());
        map.put("队列剩余长度", threadPoolTaskExecutor.getThreadPoolExecutor().getQueue().remainingCapacity());
        map.put("完成任务数", threadPoolTaskExecutor.getThreadPoolExecutor().getCompletedTaskCount());
        map.put("处理中任务数", threadPoolTaskExecutor.getThreadPoolExecutor().getActiveCount());
        log.info(map.toString());
        return map;
    }

}
