## 토비의 봄 TV 7회 스프링 리액티브 프로그래밍 (3) - Reactive Streams - Schedulers
- https://www.youtube.com/watch?v=Wlqu1xvZCak

# subscribeOn
![image](https://user-images.githubusercontent.com/55946791/114492543-c0745480-9c53-11eb-8fc6-6ff990656a97.png)
- publisher가 계속 데이터를 만들어가는 흐름 (별도의 스레드로 작업)
- subscriber가 그것을 받아서 작업처리
- `언제 사용하나요?` publisher가 아주 느린경우, 예측할수 없는경우(blocking IO로 인해서), 처리하는 쪽은 빠를때 사용

# publishOn
![image](https://user-images.githubusercontent.com/55946791/114493228-23b2b680-9c55-11eb-8410-ff2046bd770e.png)
- subscriber쪽, 받아서 데이터를 처리하는 것을 별도의 스레드에서 처리한다
- `언제 사용하나요?` 받아서 처리하는 쪽이 느릴때 사용

## 정리

|내용|publisher|subscriber|
|------|---|---|
|subscribeOn|느림,별도의 스레드로 작업|-|
|publishOn|-|느림, 별도의 스레드로 작업|


## 코드 결과
- reqest() ; 데이터 생성은 main에서 빠르게 생성한다.
- exit ; main스레드 일 끝
- subscribe는 새로운 스레드; 에서 데이터를 처리한다.
![image](https://user-images.githubusercontent.com/55946791/114493777-1b0eb000-9c56-11eb-928a-bc1d23baf0e0.png)
