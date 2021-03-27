package Ex2;

import java.util.List;
import java.util.concurrent.Flow;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/*
 * Reactive Streams - Operators
 *
 * data가 operator를 통해 계속 가공될 수 있다
 * Publisher -> [data1] -> Operator1 -> [data2] -> Op2 -> [data3] -> Subscriber
 *
 * 1. map (data1 -> function -> data2)
 * pub -> [data1] -> mapPub -> [data2] -> logSub
 *                   <- mapPub.subscribe(logSub)
 *                   -> lobSub.onSubscribe(subscription)
 *                   -> onNext
 *                   -> onNext
 *                   -> onComplete
 * */
public class PubSubOperatorsOrigin {

    public static void main(String[] args) {

        // Stream : data를 무제한으로 만들어줌. (seed값, 어떻게 만들지 function)
        Flow.Publisher<Integer> pub = iterPub(Stream.iterate(1, a -> a + 1).limit(10).collect(Collectors.toList()));
        Flow.Publisher<Integer> mapPub = mapPub(pub, (Function<Integer, Integer>) s -> s * 10);
        Flow.Publisher<Integer> mapPub2 = mapPub(mapPub, (Function<Integer, Integer>) s -> -s);
        mapPub2.subscribe(logSub());
    }

    private static Flow.Publisher<Integer> mapPub(Flow.Publisher<Integer> pub, Function<Integer, Integer> function) {
        return new Flow.Publisher<Integer>() {
            @Override
            public void subscribe(Flow.Subscriber<? super Integer> paramSubscriber) {

                // 받아온 subscriber가 아니라,
                // 새로운 subscriber를 정의해준다. 이 subscriber는 mapPub에서 data를 구독해서 function 형태로 가공한다.
                // 가공한 data를 받아온 subscriber가 구독한다.

                // 데이터를 발행하는 pub을 구독하도록 pub.subscribe를 호출
                // 구독할 새로운 subscriber를 생성
                // 데이터를 구독해서, 변형한 데이터를 구독하도록한다.
                pub.subscribe(new Flow.Subscriber<Integer>() {  // 새로운 subscriber
                    @Override
                    public void onSubscribe(Flow.Subscription s) {
                        paramSubscriber.onSubscribe(s);
                    }

                    @Override
                    public void onNext(Integer item) {
                        // 새로운 subscriber에서 데이터를 받아서 function에 의해 data를 가공하고
                        // 받아온 subscriber가 구독한다
                        paramSubscriber.onNext(function.apply(item));
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        paramSubscriber.onError(throwable);
                    }

                    @Override
                    public void onComplete() {
                        paramSubscriber.onComplete();
                    }
                });
            }
        };
    }

    private static Flow.Subscriber<Integer> logSub() {
        return new Flow.Subscriber<Integer>() {
            @Override
            public void onSubscribe(Flow.Subscription subscription) {
                System.out.println("onSubscribe: ");
                subscription.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Integer item) {
                System.out.println("onNext: " + item);
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("onError: " + throwable.getMessage());
            }

            @Override
            public void onComplete() {
                System.out.println("onComplete");
            }
        };
    }

    private static Flow.Publisher<Integer> iterPub(List<Integer> iter) {

        return new Flow.Publisher<Integer>() {
            // subscriber says : 너가 제공하는 데이터를 받겠어.
            @Override
            public void subscribe(Flow.Subscriber<? super Integer> subscriber) {
                subscriber.onSubscribe(new Flow.Subscription() {
                    // Subscription : 둘사이에 구독이 일어나는 action을 담는

                    @Override
                    public void request(long n) {
                        try {
                            iter.forEach(s -> subscriber.onNext(s));
                            subscriber.onComplete();
                        } catch (Throwable t) {
                            subscriber.onError(t);
                        }
                    }

                    @Override
                    public void cancel() {

                    }
                });
            }
        };
    }
}
