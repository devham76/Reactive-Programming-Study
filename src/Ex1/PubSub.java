package Ex1;

import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Flow;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class PubSub {

    public static void main(String[] args) throws InterruptedException {
        // Publisher <- Observable data 계속 쏴줌
        // Subscriber <- Observer

        /*
        Publisher
        한계가없음, 연속된 순서를 가지고 요소를 제공, 정보를 요청한 Subscriber한테 전달
        Publisher.subscribe(Subscriber) // Subscriber가 나한테 줘. 라고 하는 메서드
        protocol : onSubscribe(호출 필수), onNext* (0~n까지 호출가능), onError or onComplete

        publisher <---> subscription <--(demand)-- subscriber <--(event)-- publisher
        Subscription : publisher와 subscriber사이의 중계역할
        백프레셔; Publish하는 속도 조절, subscritpion의 request, cancel은 data의 양을 조절

        - Publisher 는 한 subscirption에 대해서 하나의 Thread에서만 데이터를 push 한다!!
         */

        Iterable<Integer> itr = Arrays.asList(1, 2, 3, 4, 5);
        ExecutorService es = Executors.newSingleThreadExecutor();

        Flow.Publisher p = new Flow.Publisher() {
            @Override
            public void subscribe(Flow.Subscriber subscriber) {

                Iterator<Integer> it = itr.iterator();
                subscriber.onSubscribe(new Flow.Subscription() {
                    @Override
                    public void request(long n) {
                        // Future : 자바 비동기의 기초. 비동기적으로 실행된 작업이 결과가 무엇인지를 가지고 있는 object
                        // Future 를 사용해도됨
                        // Future<?> f = es.submit(() -> {});

                        es.execute(() -> {
                            int i = 0;
                            while (i++ < n) {
                                try {
                                    if (it.hasNext())
                                        subscriber.onNext(it.next());
                                    else {
                                        subscriber.onComplete();
                                        break;
                                    }
                                } catch (RuntimeException e) {
                                    subscriber.onError(e);
                                }
                            }
                        });
                    }

                    @Override
                    public void cancel() {

                    }
                });
            }
        };

        Flow.Subscriber<Integer> s = new Flow.Subscriber<Integer>() {
            Flow.Subscription subscription;

            @Override
            public void onSubscribe(Flow.Subscription subscription) {
                System.out.println(Thread.currentThread().getName() + " onSubscribe");
                this.subscription = subscription;
                this.subscription.request(1);
            }

            @Override
            public void onNext(Integer item) {
                System.out.println(Thread.currentThread().getName() + " onNext : " + item);
                this.subscription.request(1);
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("onError: "+throwable.getMessage());
            }

            @Override
            public void onComplete() {
                System.out.println("onComplete");
            }
        };

        p.subscribe(s);
        es.awaitTermination(10, TimeUnit.HOURS);
        es.shutdown();

    }
}
