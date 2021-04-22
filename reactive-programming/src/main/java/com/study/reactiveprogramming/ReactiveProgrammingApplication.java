/**
 * 토비의 봄 TV 12회 스프링 리액티브 프로그래밍 (8) WebFlux
 * https://www.youtube.com/watch?v=ScH7NZU_zvk
 */
package com.study.reactiveprogramming;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@EnableAsync    // 비동기(다른 스레드)
@RestController
@SpringBootApplication
public class ReactiveProgrammingApplication {
    static final String URL1 = "http://localhost:8090/service1?req={req}";
    static final String URL2 = "http://localhost:8090/service2?req={req}";

    @Autowired
    MyService myService;

    WebClient client = WebClient.create();

    @GetMapping("/")
    Mono<String> hello() {

        log.info("position1");
        // Mono.just -> 데이터를 미리 만들어놓음.
        // subscribe되기 전에 미 publish할 데이터를 만들어놓는다!!!!

        // Mono를 만든다 : publisher를만든다. (subscriber가 subscribe해줘야 데이터가 흐른다)
        // spring이 알아서 subscribe해준다.

        /*
         * 별도의 스레드를 통하지 않고, 즉시 실행된다. 즉, 동기적으로 실행된다.
         * log.info가 실행되고 나서 스프링이 mono를 동작시킨다.
         * */
        Mono m = Mono.just("hello webflux").log();
        log.info("position2");
        return m;
    }

    /*
     * [cold type]
     * - mono가 실행되기 전에 미리 데이터가 생성된다.
     * - cold source의 publisher는 여러번 subscribe해도 똑같은 데이터
     * pos1->method generateHello()->pos2->doOnNext : method generateHello()
     * */
    @GetMapping("/test")
    public Mono<String> helloTest() {
        log.info("pos1");
        Mono<String> m = Mono.just(generateHello()).doOnNext(c -> log.info("doOnNext : " + c)).log();
        String msg = m.block(); // Mono에서 string 타입으로 뺀다.
        log.info("pos2, " + msg);
        return m;
    }

    /*
     * [hot type]
     * - pos1->pos2->method generateHello() -> doOnNext: method generateHello()
     * - subscribe 할때 publish할 데이터가 생성된다.
     * - 실시간으로 데이터가 생성..
     * - 구독하는 시점에서의 데이터만 가져온다.
     * */
    @GetMapping("/test2")
    public Mono<String> helloTest2() {
        log.info("pos1");
        // supplier : 파라미터는 없고 return만있는 함수형 인터페이스
        Mono m = Mono.fromSupplier(() -> generateHello()).doOnNext(c -> log.info("doOnNext : " + c)).log();
        m.subscribe();  // 하나 이상의 subscribe가 가능하다.
        log.info("pos2");
        return m;
    }

    private String generateHello() {
        log.info("method generateHello()");
        return "Hello Mono";
    }


    @GetMapping("/rest")
    public Mono<String> rest(@RequestParam int req) {
        //spring이 알아서 subscribe호출해준다.
        // reactive code style 장점 ; 중간중간 끼어들기 좋다.
        return client.get().uri(URL1, req).exchange()   // return ; Mono<ClientResponse>
                .doOnSuccess(c -> log.info("[test] " + c.toString()))
                .flatMap(c -> c.bodyToMono(String.class))  // Mono<String>
                .flatMap(res1 -> client.get().uri(URL2, res1).exchange())   // Mono<ClientResponse>
                .flatMap(c -> c.bodyToMono(String.class))  // Mono<String>
                .doOnSuccess(c -> log.info("[test] " + c.toString()))
                .flatMap(res2 -> Mono.fromCompletionStage(
                        myService.work(res2)))   // CompletableFuture<String> -> Mono<Stirng>
                .doOnSuccess(c -> log.info("[test] " + c.toString()));

    }

    @GetMapping("/service1")
    public Mono<String> service1(@RequestParam String req) {
        return Mono.just(" service1 : " + req);
    }

    @GetMapping("/service2")
    public Mono<String> service2(@RequestParam String req) {
        return Mono.just(" service2 : " + req);
    }

    public static void main(String[] args) {
        SpringApplication.run(ReactiveProgrammingApplication.class, args);
    }

    @Service
    public static class MyService {

        // 비동기 리턴 가능. String -> Future , CompletableFuture...등으로 returen해야함.
        @Async
        public CompletableFuture<String> work(String req) {
            return CompletableFuture.completedFuture(req + "/asyncwork");
        }
    }
}
