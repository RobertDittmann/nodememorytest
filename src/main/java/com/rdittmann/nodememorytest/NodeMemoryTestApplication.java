package com.rdittmann.nodememorytest;

import com.rdittmann.nodememorytest.services.TestedService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.concurrent.TimeUnit.DAYS;

@Slf4j
@SpringBootApplication
public class NodeMemoryTestApplication {

    private static final int sleepTime = 5000; // 5 seconds

    private static String[] args;

    @SneakyThrows
    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler(NodeMemoryTestApplication::uncaughtExceptionHandler);

        val applicationContext = SpringApplication.run(NodeMemoryTestApplication.class, args);
        val testedService = applicationContext.getBeanFactory().getBean(TestedService.class);

        NodeMemoryTestApplication.args = args;

        // start test
        ExecutorService executorService = Executors.newFixedThreadPool(250);
        ExecutorService executorService2 = Executors.newFixedThreadPool(250);
        ExecutorService executorService3 = Executors.newFixedThreadPool(250);
        ExecutorService executorService4 = Executors.newFixedThreadPool(250);
        ExecutorService executorService5 = Executors.newFixedThreadPool(250);

        final Instant start = Instant.now();
        testedService.getData("parameter", "parameter"); // 1st call should not throw exception

        log.info("START");

        int j = 1000;
        int i = 0;
        for (; i < j; ) {
            runTest(testedService, Arrays.asList(executorService, executorService2, executorService3, executorService4, executorService5), start, i, j);
            i = j;
            j = j + 1000;
        }

        executorService.awaitTermination(100, DAYS);
        log.info("END");
    }

    private static void runTest(TestedService testedService, List<ExecutorService> executorServices, Instant start, int i, int i2) throws InterruptedException {
        increaseLoad(testedService, executorServices, i, i2);
        Thread.sleep(NodeMemoryTestApplication.sleepTime);
        log.info("Load increased after " + Instant.now().minus(start.getEpochSecond(), ChronoUnit.SECONDS).getEpochSecond() + " seconds, with total OK requests: " + TestedService.okRequests.get() + ", with total FAILED requests: " + TestedService.failedRequests.get());
    }

    private static void increaseLoad(TestedService testedService, List<ExecutorService> executorServices, int i2, int i3) {
        for (int i = i2; i < i3; i++) {
            int finalI = i;
            executorServices.forEach(executorService -> executorService.submit(() -> {
                for (int j = 0; j < Integer.MAX_VALUE; j++) {
                    if (Objects.nonNull(args) && args.length > 0 && args[0].toLowerCase().equals("rest")) {
                        testedService.restCall(String.valueOf(finalI), String.valueOf(finalI));
                    } else {
                        testedService.getData("parameter" + finalI, "parameter" + finalI);
                    }
                }
            }));
        }
    }

    private static void uncaughtExceptionHandler(Thread t, Throwable e) {
        log.error(String.format("Uncaught exception in thread:'%s' :", t.getName()), e);
        System.exit(-1);
    }

}
