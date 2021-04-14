package lecture.day4;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import lombok.extern.slf4j.Slf4j;

/*
* 비동기 결과를 가져오는 방법 : Future, callback
* */

@Slf4j
public class FutureEx {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        // 스레드풀 : 스레드를 생성,파괴는 큰 작업. 따라서 미리 스레드를 만들어서 재활용하자.
        ExecutorService executorService = Executors.newCachedThreadPool();

        Future<String> future = executorService.submit(() -> {
            Thread.sleep(2000);
            log.info("Async");
            return "hello";
        });

        log.info(String.valueOf(future.isDone()));
        Thread.sleep(2100);
        log.info("exit");

        log.info(String.valueOf(future.isDone()));
        log.info(future.get());
    }
}
