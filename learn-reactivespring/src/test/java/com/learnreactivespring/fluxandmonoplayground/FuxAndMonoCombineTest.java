package com.learnreactivespring.fluxandmonoplayground;

import org.junit.Test;
//import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.time.Duration;

public class FuxAndMonoCombineTest {

    @Test
    public void combineUsingMerge(){
        Flux<String> flux1=Flux.just("A","B","C");
        Flux<String> flux2=Flux.just("D","E","F");
        Flux<String> mergeFlux=Flux.merge(flux1,flux2);

        StepVerifier.create(mergeFlux.log())
                .expectSubscription()
                .expectNext("A","B","C","D","E","F")
                .verifyComplete();
    }

    @Test
    public void combineUsingMergeWithDelay(){

        //implementing virtual time
        VirtualTimeScheduler.getOrSet();

        Flux<String> flux1=Flux.just("A","B","C").delayElements(Duration.ofSeconds(1));
        Flux<String> flux2=Flux.just("D","E","F").delayElements(Duration.ofSeconds(1));
        Flux<String> mergeFlux=Flux.merge(flux1,flux2);

      /*  StepVerifier.create(mergeFlux.log())
                .expectSubscription()
                .expectNextCount(6)
              //  .expectNext("A","B","C","D","E","F")
                .verifyComplete();*/
        StepVerifier.withVirtualTime(()->mergeFlux.log())
                    .expectSubscription()
                .thenAwait(Duration.ofSeconds(3))
                .expectNextCount(6)
                .verifyComplete();
    }

    @Test
    public void combineUsingConcat(){
        Flux<String> flux1=Flux.just("A","B","C");
        Flux<String> flux2=Flux.just("D","E","F");
        Flux<String> mergeFlux=Flux.concat(flux1,flux2);

        StepVerifier.create(mergeFlux.log())
                .expectSubscription()
                .expectNext("A","B","C","D","E","F")
                .verifyComplete();
    }

    @Test
    public void combineUsingConcat_withDelay(){
//implementing virtual time
        VirtualTimeScheduler.getOrSet();
        Flux<String> flux1=Flux.just("A","B","C").delayElements(Duration.ofSeconds(1));
        Flux<String> flux2=Flux.just("D","E","F").delayElements(Duration.ofSeconds(1));
        Flux<String> mergeFlux=Flux.concat(flux1,flux2);

//        StepVerifier.create(mergeFlux.log())
//                .expectSubscription()
//                .expectNext("A","B","C","D","E","F")
//                .verifyComplete();

        StepVerifier.withVirtualTime(()->mergeFlux.log())
                    .expectSubscription()
                    .thenAwait(Duration.ofSeconds(6))
                    .expectNextCount(6)
                    .verifyComplete();
    }

    @Test
    public void combineUsingZip(){
        Flux<String> flux1=Flux.just("A","B","C");
        Flux<String> flux2=Flux.just("D","E","F");
        Flux<String> mergeFlux=Flux.zip(flux1,flux2,(t1,t2)->{ //(A,D), (B,E),(C,F)
            return t1.concat(t2);//AD,BE,CF
        });

        StepVerifier.create(mergeFlux.log())
                .expectSubscription()
                .expectNext("AD","BE","CF")
                .verifyComplete();
    }


}
