package lecture.day3;

import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

public class SchedulerEx {
    public static void main(String[] args) {

        // blocking 구조.
        // 각스레드가 해당 스레드를 기다리고 있음. 금방 스레드가 가득차고, 큐까지 가득차서 서비스를 처리할수없는 지경까지 이를수있음
        // 실제 서비스에서는 아래처럼 publisher와 subscriber를 함께 작성하지 않는다.
        Publisher<Integer> publisher = subscriber -> subscriber.onSubscribe(new Subscription() {
            @Override
            public void request(long n) {
                // onNext가 전부 실행이 되어야 subscriber가 구독을 시작한다.
                // blocking방식은 문제점이 많으므로, 이 작업 자체를 별도의 스레드에서 일어나도록 만든다.
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

        publisher.subscribe(new Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription subscription) {
                System.out.println("onSubscribe");
                subscription.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Integer item) {
                System.out.println("onNext : " + item);
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("onError: " + throwable);
            }

            @Override
            public void onComplete() {
                System.out.println("onComplete");
            }
        });
    }
}
