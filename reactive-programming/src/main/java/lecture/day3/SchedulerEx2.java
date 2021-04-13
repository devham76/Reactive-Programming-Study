package lecture.day3;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SchedulerEx2 {
    public static void main(String[] args) {

        // blocking 구조.
        // 각스레드가 해당 스레드를 기다리고 있음. 금방 스레드가 가득차고, 큐까지 가득차서 서비스를 처리할수없는 지경까지 이를수있음
        // 실제 서비스에서는 아래처럼 publisher와 subscriber를 함께 작성하지 않는다.
        Publisher<Integer> publisher = subscriber -> subscriber.onSubscribe(new Subscription() {
            @Override
            public void request(long n) {
                // onNext가 전부 실행이 되어야 subscriber가 구독을 시작한다.
                // blocking방식은 문제점이 많으므로, 이 작업 자체를 별도의 스레드에서 일어나도록 만든다.
                log.info("request()");
                subscriber.onNext(1);
                subscriber.onNext(2);
                subscriber.onNext(3);
                subscriber.onNext(4);
                subscriber.onComplete();
            }

            @Override
            public void cancel() {

            }
        });

        /**
         * subscribOn
         * publish가 아주느린 경우 사용 - 새로운 스레드로 publisher
         * */
        // 위의 pub과 아래의sub을 연결해주는 operator
        Publisher<Integer> subOnPub = subscriber -> {
            ExecutorService executorService = Executors.newSingleThreadExecutor(); // 한번에 한개의 스레드만 동작한다

            executorService.execute(() -> publisher.subscribe(subscriber));
        };

        /**
         * publishOn용
         * subscribe가 아주느린 경우 사용 - 새로운 스레드로 subscriber
         * */
        Publisher<Integer> pubOnSub = subscriber -> {
            subOnPub.subscribe(new Subscriber<Integer>() {
                // subscriber작업을 별개의 쓰레드에서 실행시킨다
                ExecutorService executorService = Executors.newSingleThreadExecutor();

                @Override
                public void onSubscribe(Subscription subscription) {
                    subscriber.onSubscribe(subscription);
                }

                @Override
                public void onNext(Integer item) {
                    executorService.execute(() -> subscriber.onNext(item));
                }

                @Override
                public void onError(Throwable throwable) {
                    executorService.execute(() -> subscriber.onError(throwable));
                }

                @Override
                public void onComplete() {
                    executorService.execute(() -> subscriber.onComplete());
                }
            });
        };

        pubOnSub.subscribe(new Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription subscription) {
                log.info("onSubscribe");
                subscription.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Integer item) {
                log.info("onNext : " + item);
            }

            @Override
            public void onError(Throwable throwable) {
                log.info("onError: " + throwable);
            }

            @Override
            public void onComplete() {
                log.info("onComplete");
            }
        });
        System.out.println("exit");
    }
}
