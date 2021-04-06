package Ex2;

import java.util.List;
import java.util.concurrent.Flow;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PubSubOperatorsClean {
    public static void main(String[] args) {

        Flow.Publisher<Integer> pub = iterPub(Stream.iterate(1, a -> a + 1).limit(10)
                                                    .collect(Collectors.toList()));
        //Flow.Publisher<Integer> mapPub = mapPub(pub, (Function<Integer, Integer>) s -> s * 10);
        //Flow.Publisher<Integer> sumPub = sumPub(pub);
        // BiFunction: 인수2개받기
        //Flow.Publisher<Integer> reducePub = reducePub(pub, " ", (BiFunction<Integer, Integer, Integer>) (a, b) -> a +"-"+ b);

        Flow.Publisher<Integer> generalPub = generalPub(pub, (Function<Integer, Integer>) s -> s * 10);
        Flow.Publisher<String> stringPub = generalPub(pub, s -> "[" + s + "]");
        Flow.Publisher<String> reducePub = reducePub(pub, "", (a, b) -> a +"-"+ b);

        reducePub.subscribe(logSub());
    }

    private static <T, R> Flow.Publisher<R> reducePub(Flow.Publisher<T> pub,
                                                      R init,
                                                      BiFunction<R, T, R> bf) {
        return new Flow.Publisher<R>() {
            @Override
            public void subscribe(Flow.Subscriber<? super R> subscriber) {
                pub.subscribe(new DelegateSubClean<T, R>(subscriber) {
                    R result = init;

                    @Override
                    public void onNext(T item) {
                        result = bf.apply(result, item);
                    }

                    @Override
                    public void onComplete() {
                        subscriber.onNext(result);
                        subscriber.onComplete();
                    }
                });
            }
        };

    }

    // T타입이 들어와서 R타입으로 리턴
    private static <T, R> Flow.Publisher<R> generalPub(Flow.Publisher<T> pub, Function<T, R> function) {
        return new Flow.Publisher<R>() {
            @Override
            public void subscribe(Flow.Subscriber<? super R> subscriber) {
                pub.subscribe(new DelegateSubClean<T, R>(subscriber) {
                    @Override
                    public void onNext(T item) {
                        subscriber.onNext(function.apply(item));
                    }
                });
            }
        };

    }

    private static Flow.Publisher<Integer> sumPub(Flow.Publisher<Integer> pub) {
        return new Flow.Publisher<Integer>() {
            @Override
            public void subscribe(Flow.Subscriber<? super Integer> subscriber) {
                pub.subscribe(new DelegateSub(subscriber) {
                    int sum = 0;

                    @Override
                    public void onNext(Integer item) {
                        // 내려오는 data를 받을때마다 sum에 더해준다.
                        sum += item;
                    }

                    @Override
                    public void onComplete() {
                        // 내려오는 data를 모두 받았을때, sum을 넘기고 완료한다.
                        super.onNext(sum);
                        super.onComplete();
                    }
                });
            }
        };
    }

    private static Flow.Publisher<Integer> mapPub(Flow.Publisher<Integer> pub,
                                                  Function<Integer, Integer> function) {
        return new Flow.Publisher<Integer>() {
            @Override
            public void subscribe(Flow.Subscriber<? super Integer> paramSubscriber) {
                pub.subscribe(new DelegateSub(paramSubscriber) {
                    @Override
                    public void onNext(Integer item) {
                        super.onNext(function.apply(item));
                    }
                });
            }
        };
    }

    private static <T> Flow.Subscriber<T> logSub() {
        return new Flow.Subscriber<T>() {
            @Override
            public void onSubscribe(Flow.Subscription subscription) {
                System.out.println("onSubscribe: ");
                subscription.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(T item) {
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
