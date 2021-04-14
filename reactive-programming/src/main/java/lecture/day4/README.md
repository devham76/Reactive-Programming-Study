## 토비의 봄 TV 8회 스프링 리액티브 프로그래밍 (4) 자바와 스프링의 비동기 기술
- https://www.youtube.com/watch?v=aSTuQiPB4Ns


### public interface Future<V>
비동기 적인 무엇인가 작업을 실행하고, 그것에 대한 결과를 가지고있는것
기존의 스레드말고, 새로운 스레드에서 별개의 작업을 하고, 그 결과를 다른 방법으로 가져와야하는데 가장 기본적인방법이 
future interface이다.