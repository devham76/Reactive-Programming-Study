package lecture.day3;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@Slf4j
public class FluxScEx {
    public static void main(String[] args) throws InterruptedException {
        // 1~10까지 숫자 pub
        /*
        Flux.range(1, 10)
            .publishOn(Schedulers.newSingle("pub"))
            .log()
            .subscribeOn(Schedulers.newSingle("sub"))
            .subscribe(System.out::println);

        System.out.println("exit");
         */

        // 별도의 스레드가 떠서 동작
        // 일정 시간을 인터벌 무한대로 숫자를 쏴준다.
        Flux.interval(Duration.ofMillis(500))
            .take(10)   // 10개 데이터 받으면 종료
            .subscribe(s->log.info("onNext:{}",s));

        TimeUnit.SECONDS.sleep(10);


        // jvm은 user thread가있으면 죽지않음.
        // 대신, daemon thread만 있으면 죽음.
        // Flux.interval은 daemon thread임
        // TimeUnit.SECONDS.sleep 는 user thread임
        // user thread
        // daemon thread
    }
}
