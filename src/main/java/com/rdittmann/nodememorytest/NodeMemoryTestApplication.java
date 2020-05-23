package com.rdittmann.nodememorytest;

import com.rdittmann.nodememorytest.services.TestedService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@SpringBootApplication
public class NodeMemoryTestApplication {

    private static final int sleepTime = 600000; // 10 minutes

    private static String[] args;

    @SneakyThrows
    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler(NodeMemoryTestApplication::uncaughtExceptionHandler);

        val applicationContext = SpringApplication.run(NodeMemoryTestApplication.class, args);
        val testedService = applicationContext.getBeanFactory().getBean(TestedService.class);

        NodeMemoryTestApplication.args = args;

        // start test
        ExecutorService executorService = Executors.newFixedThreadPool(1000);

        final Instant start = Instant.now();
        testedService.getData("parameter", "parameter"); // 1st call should not throw exception

        log.info("START");

        int j = 100;
        int i = 0;
        for (; i < j; ) {
            runTest(testedService, executorService, start, i, j);
            i = j;
            j = j + 1000;
        }

        executorService.awaitTermination(100, TimeUnit.DAYS);
        log.info("END");
    }

    private static void runTest(TestedService testedService, ExecutorService executorService, Instant start, int i, int i2) throws InterruptedException {
        increaseLoad(testedService, executorService, i, i2);
        Thread.sleep(NodeMemoryTestApplication.sleepTime);
        log.info("Load increased after " + Instant.now().minus(start.getEpochSecond(), ChronoUnit.SECONDS).getEpochSecond() / 60 + " minutes");
    }

    private static void increaseLoad(TestedService testedService, ExecutorService executorService, int i2, int i3) {
        for (int i = i2; i < i3; i++) {
            int finalI = i;
            executorService.submit(() -> {
                for (int j = 0; j < Integer.MAX_VALUE; j++) {
                    if (Objects.nonNull(args) && args.length > 0 && args[0].toLowerCase().equals("rest")) {
                        testedService.restCall(String.valueOf(finalI), String.valueOf(finalI));
                    } else {
                        testedService.getData("parameter" + finalI, "parameter" + finalI);
                    }
                }
            });
        }
    }

    private static void uncaughtExceptionHandler(Thread t, Throwable e) {
        log.error(String.format("Uncaught exception in thread:'%s' :", t.getName()), e);
        System.exit(-1);
    }

}
