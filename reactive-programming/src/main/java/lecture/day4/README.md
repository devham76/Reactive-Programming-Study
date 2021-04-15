# 토비의 봄 TV 8회 스프링 리액티브 프로그래밍 (4) 자바와 스프링의 비동기 기술
- https://www.youtube.com/watch?v=aSTuQiPB4Ns

## 자바에서의 비동기
- JVM에서 비동기 코드를 어떻게 개발할 수 있을까?
- 자바는 비동기 프로그래밍을 위한 두가지 모델을 제공한다.

|종류|설명|
|---|---|
|Callbacks|비동기 메서드는 반환 값을 가질 수 없지만, 외부 callback매개 변수 (람다나 익명 클래스)를 통해 결과를 호출받을 수 있다.<br> (ex : Swing의 EventListener)  | 
|Futures|비동기 메서드는 Future를 직시 리턴한다. 비동기 프로세스는 T value를 계산하지만 Future개체는 이에 대한 access를 래핑한다. 값을 즉시 사용할 수 없으며 사용할 수 있을 때까지 값을 polling할 수 있다.<br> (ex: Callable 작업을 실행하는 ExecutorService는 Future 개체를 사용한다.) | 

### public interface Future<V>
비동기 적인 무엇인가 작업을 실행하고, 그것에 대한 결과를 가지고있는것
기존의 스레드말고, 새로운 스레드에서 별개의 작업을 하고, 그 결과를 다른 방법으로 가져와야하는데 가장 기본적인방법이 
future interface이다.

