# 토비의 봄 TV 6회 스프링 리액티브 프로그래밍 (2) - Reactive Streams - Operators

API Components
- Publisher
- Subscriber
- Subscription
- Processor

Protocol
- onSubscribe
- onNext*
- optional : (onError | onComplete)?


## 개
![image](https://user-images.githubusercontent.com/55946791/113723463-56781e80-972c-11eb-9aba-bca5f6636bef.png)

- Subscriber는 subscription.request(long n)을 통해 이벤트를 요청하고
- Publisher는 직접 Subscriber에게 onNext()호출을 통해 event를 전달한다.


## Publisher
```java
public interface Publisher<T> { 
    void subscribe(Subscriber<? super T> var1); 
}
```

## Subscriber
```java
public interface Subscriber<T> { 
    void onSubscribe(Subscription var1); 
    void onNext(T var1); 
    void onError(Throwable var1); 
    void onComplete(); }
```

## subscription
```java
public interface Subscription { 
    void request(long var1); 
    void cancel(); 
}
```




### Reference
- https://www.youtube.com/watch?v=DChIxy9g19o
- https://phantasmicmeans.tistory.com/entry/Reactive-Streams-Publisher-Subscriber-Subscription
