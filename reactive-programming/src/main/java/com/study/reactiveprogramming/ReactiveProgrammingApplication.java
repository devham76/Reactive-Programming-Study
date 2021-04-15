/**
 * 토비의 봄 TV 12회 스프링 리액티브 프로그래밍 (8) WebFlux
 * https://www.youtube.com/watch?v=ScH7NZU_zvk
 * */
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
