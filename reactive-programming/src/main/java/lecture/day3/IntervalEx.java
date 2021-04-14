package lecture.day3;

import java.util.concurrent.Executors;
import java.util.concurrent.Flow;
import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

/*
 * operator가 하는일 : 데이터를 변환, 스케쥴링, publishing을 control (take)
 * */
@Slf4j
public class IntervalEx {
    public static void main(String[] args) {

        Publisher<Integer> publisher = subscriber -> {
            subscriber.onSubscribe(new Subscription() {
                int num = 0;
                boolean cancelled = false;

                @Override
                public void request(long n) {
                    ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
                    // 어떤 작업을 시간을 두고 계속 진행
                    exec.scheduleAtFixedRate(() -> {
                        if (cancelled) {
                            exec.shutdown();
                            return;
                        }
                        subscriber.onNext(num++);
                    }, 0, 300, TimeUnit.MICROSECONDS);

                }

                @Override
                public void cancel() {
                    cancelled = true;
                }
            });
        };

        Publisher<Integer> takePub = subscriber -> {
            publisher.subscribe(new Subscriber<Integer>() {
                int count = 0;
                Subscription subscription;

                @Override
                public void onSubscribe(Subscription subscription) {
                    this.subscription = subscription;
                    subscriber.onSubscribe(subscription);
                }

                @Override
                public void onNext(Integer item) {
                    subscriber.onNext(item);
                    if (++count > 10) {
                        subscription.cancel();
                    }
                }

                @Override
                public void onError(Throwable throwable) {
                    subscriber.onError(throwable);
                }

                @Override
                public void onComplete() {
                    subscriber.onComplete();
                }
            });
        };

        takePub.subscribe(new Subscriber<Integer>() {
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
    }
}
