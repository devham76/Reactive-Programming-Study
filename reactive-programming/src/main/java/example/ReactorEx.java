package example;

import reactor.core.publisher.Flux;

public class ReactorEx {
    public static void main(String[] args) {
        // Flux : publisher의 일종
        Flux.<Integer>create(e -> {
            e.next(10);
            e.next(20);
            e.next(30);
            e.complete();
        })
            .log() // 위와 아래를 연결하면서 어떻게 메소드를 출력하는지 확인가능
            .map(s -> s * 10)
            .reduce(0, (a,b) -> a+b)
            .log()
            .subscribe(System.out::println);
    }
}
/*
* 우리가 publisher를 만들고 던지면,
* 스프링이 알아서 subscribe를 만든다
*
*
* */
