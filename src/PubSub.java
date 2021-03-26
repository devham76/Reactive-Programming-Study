import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.Flow;

public class PubSub {

    public static void main(String[] args) {
        // Publisher <- Observable data 계속 쏴줌
        // Subscriber <- Observer

        /*
        Publisher
        한계가없음, 연속된 순서를 가지고 요소를 제공, 정보를 요청한 Subscriber한테 전달
        Publisher.subscribe(Subscriber) // Subscriber가 나한테 줘. 라고 하는 메서드
        protocol : onSubscribe(호출 필수), onNext* (0~n까지 호출가능), onError or onComplete

        publisher <---> subscription <--(demand)-- subscriber <--(event)-- publisher
        subscirption은 publisher와 subscriber사이의 중계역할
        백프레셔; Publish하는 속도 조절, subscritpion의 request, cancel은 data의 양을 조절
         */

        Iterable<Integer> itr = Arrays.asList(1, 2, 3, 4, 5);

        Flow.Publisher p = new Flow.Publisher() {
            @Override
            public void subscribe(Flow.Subscriber subscriber) {
                Iterator<Integer> it = itr.iterator();
                subscriber.onSubscribe(new Flow.Subscription() {
                    @Override
                    public void request(long n) {
                        while (n-- >0) {
                            if (it.hasNext())
                                subscriber.onNext(it.next());
                            else {
                                subscriber.onComplete();
                                break;
                            }
                        }
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
                System.out.println("onSubscribe");
                this.subscription = subscription;
                this.subscription.request(1);
            }

            @Override
            public void onNext(Integer item) {
                System.out.println("onNext : " + item);
                this.subscription.request(1);
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("onError");
            }

            @Override
            public void onComplete() {
                System.out.println("onComplete");
            }
        };

        p.subscribe(s);
    }
}
