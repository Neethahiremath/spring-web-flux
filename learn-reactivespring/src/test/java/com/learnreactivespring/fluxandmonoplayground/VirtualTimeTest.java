package com.learnreactivespring.fluxandmonoplayground;

import org.junit.Test;
//import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.time.Duration;

public class VirtualTimeTest {

    //How to virtualize time while running test cases
    @Test
    public void testingWithoutVirtualTime(){
        Flux<Long> longFlux=Flux.interval(Duration.ofSeconds(1))
                .take(3);
//here it takes our laptop clock to execute the testcase so it takes 3 seconds to execute complete test case

        StepVerifier.create(longFlux.log())
                .expectSubscription()
                .expectNext(0L,1L,2L)
                .verifyComplete();


    }

    @Test
    public void testingWithVirtualTime(){
        VirtualTimeScheduler.getOrSet();
        Flux<Long> longFlux=Flux.interval(Duration.ofSeconds(1))
                .take(3);
//here it takes our laptop clock to execute the testcase so it takes 3 seconds to execute complete test case
//this helps to save our build time during build process

        StepVerifier.withVirtualTime(()->longFlux.log())

                .expectSubscription()
                .thenAwait(Duration.ofSeconds(3))
                .expectNext(0L,1L,2L)
                .verifyComplete();


    }
}
