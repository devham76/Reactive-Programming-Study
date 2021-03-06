package lecture.day2;

import java.util.concurrent.Flow;

public class DelegateSubClean<T,R> implements Flow.Subscriber<T> {
    Flow.Subscriber subscriber;

    public DelegateSubClean(Flow.Subscriber<? super R> subscriber) {
        this.subscriber = subscriber;

    }
    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        subscriber.onSubscribe(subscription);
    }

    @Override
    public void onNext(T item) {
        subscriber.onNext(item);
    }

    @Override
    public void onError(Throwable throwable) {
        subscriber.onError(throwable);
    }

    @Override
    public void onComplete() {
        subscriber.onComplete();
    }

}
