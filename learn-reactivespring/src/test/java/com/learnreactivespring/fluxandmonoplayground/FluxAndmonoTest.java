package com.learnreactivespring.fluxandmonoplayground;
//import org.junit.jupiter.api.Test;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class FluxAndmonoTest {
    @Test
    public void fluxTest(){
        Flux<String> stringFlux = Flux
                .just("Spring", "SpringBoot", "Reactive Spring")
                //.concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .concatWith(Flux.just("AfterErrorEvent"))
                .log();

        stringFlux.subscribe(System.out::println,
                (e)->System.err.println("Exception is: "+e)
        ,()-> System.out.println("Completed"));
    }

    @Test
    public void fluxTestElementsWithoutError(){
    Flux<String> stringFlux = Flux
            .just("Spring", "SpringBoot", "Reactive Spring")
            .log();
    StepVerifier.create(stringFlux)
        .expectNext("Spring")
        .expectNext("SpringBoot")
        .expectNext("Reactive Spring")
        .verifyComplete();

}

    @Test
    public void fluxTestElementsWithError(){
        Flux<String> stringFlux = Flux
                .just("Spring", "SpringBoot", "Reactive Spring")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .log();
        StepVerifier.create(stringFlux)
                .expectNext("Spring")
                .expectNext("SpringBoot")
                .expectNext("Reactive Spring")
                //.expectError(RuntimeException.class)
                .expectErrorMessage("Exception Occurred")
                .verify();
    }

    @Test
    public void fluxTestElementsCountWithError(){
        Flux<String> stringFlux = Flux
                .just("Spring", "SpringBoot", "Reactive Spring")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .log();
        StepVerifier.create(stringFlux)
               .expectNextCount(3)
                //.expectError(RuntimeException.class)
                .expectErrorMessage("Exception Occurred")
                .verify();
    }

    @Test
    public void fluxTestElementsWithError1(){
        Flux<String> stringFlux = Flux
                .just("Spring", "SpringBoot", "Reactive Spring")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .log();
        StepVerifier.create(stringFlux)
                .expectNext("Spring","SpringBoot","Reactive Spring")
                //.expectError(RuntimeException.class)
                .expectErrorMessage("Exception Occurred")
                .verify();
    }


    @Test
    public void monoTest(){
        Mono<String> stringMono=Mono.just("Spring");

        StepVerifier.create(stringMono.log())
                .expectNext("Spring")
                .verifyComplete();
    }


    @Test
    public void monoTestWithError(){

        StepVerifier.create(Mono.error(new RuntimeException("Exception Occurred")))
                .expectError(RuntimeException.class)
                .verify();
    }
}
