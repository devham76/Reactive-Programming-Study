package lecture.day4;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import lombok.extern.slf4j.Slf4j;

/*
비동기 작업의 결과를 넘기는 방법
* Future : 작업의 결과를 담고 있다. (blocking)
* Callback
* */
@Slf4j
public class FutureEx2 {

    interface SuccessCallback {
        void onSuccess(String result);
    }

    interface ExceptionCallback {
        void onError(Throwable t);
    }

    public static class CallbackFutureTask extends FutureTask<String> {
        SuccessCallback successCallback;
        ExceptionCallback exceptionCallback;

        public CallbackFutureTask(Callable<String> callable, SuccessCallback successCallback,
                                  ExceptionCallback exceptionCallback) {
            super(callable);
            this.successCallback = Objects.requireNonNull(successCallback); // 값있으면 그값, null이면 throw npe
            this.exceptionCallback = Objects.requireNonNull(exceptionCallback);
        }

        @Override
        protected void done() {
            try {
                successCallback.onSuccess(get());
            } catch (InterruptedException e) {  // 작업수행하지말고 종료해라 라는 시그널을 준다.
                Thread.currentThread().interrupt();
            } catch (ExecutionException e) {    // 비동기작업 수행하다가 예외가 발생했어
                exceptionCallback.onError(e.getCause());
            }
        }
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        // 스레드풀 : 스레드를 생성,파괴는 큰 작업. 따라서 미리 스레드를 만들어서 재활용하자.
        ExecutorService executorService = Executors.newCachedThreadPool();

        CallbackFutureTask callbackFutureTask = new CallbackFutureTask(() -> {
            Thread.sleep(2000);
            //if (1 == 1) { throw new RuntimeException("Async ERROR!!"); }
            log.info("Async");
            return "Hello";
        }
                , result -> log.info(result)
                , e -> log.info("Error: {}", e.getMessage()));

        /*
        FutureTask<String> futureTask = new FutureTask<String>(() -> {
            Thread.sleep(2000);
            log.info("Async");
            return "Hello";
        }) {
            @Override
            protected void done() {
                try {
                    log.info(get());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        };

        executorService.execute(futureTask);
        */
        executorService.execute(callbackFutureTask);
        executorService.shutdown();
    }
}
