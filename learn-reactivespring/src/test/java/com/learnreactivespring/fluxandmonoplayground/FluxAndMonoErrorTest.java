package com.learnreactivespring.fluxandmonoplayground;

import com.learnreactivespring.fluxandmonoplayground.exception.CustomException;
import org.junit.Test;
//import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class FluxAndMonoErrorTest {

    @Test
    public void fluxErrorHandling(){

        Flux<String> stringFlux=Flux.just("A","B","C")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .concatWith(Flux.just("0"))
                .onErrorResume((e)->{ // this block gets executed and returns the default flux
                    System.out.println("Exception is:"+e);
                    return Flux.just("default","default1");
                });

        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A","B","C")
              //  .expectError(RuntimeException.class)
               // .verify();
                .expectNext("default","default1")
                .verifyComplete();
    }

    @Test
    public void fluxErrorHandling_OnErrorReturn(){

        Flux<String> stringFlux=Flux.just("A","B","C")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .concatWith(Flux.just("0"))
                .onErrorReturn("default");

        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A","B","C")
                //  .expectError(RuntimeException.class)
                // .verify();
                .expectNext("default")
                .verifyComplete();
    }

    @Test
    public void fluxErrorHandling_OnErrorMap(){

        Flux<String> stringFlux=Flux.just("A","B","C")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .concatWith(Flux.just("0"))
                .onErrorMap((e)->new CustomException(e));

        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A","B","C")
                //  .expectError(RuntimeException.class)
                // .verify();
                .expectError(CustomException.class)
                .verify();
    }

    @Test
    public void fluxErrorHandling_withRetry(){

        Flux<String> stringFlux=Flux.just("A","B","C")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .concatWith(Flux.just("0"))
                .onErrorMap((e)->new CustomException(e))
                .retry(2);

        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A","B","C")
                .expectNext("A","B","C")
                .expectNext("A","B","C")
                //  .expectError(RuntimeException.class)
                // .verify();
                .expectError(CustomException.class)
                .verify();
    }

    @Test
    public void fluxErrorHandling_withRetryBackoff(){

        Flux<String> stringFlux=Flux.just("A","B","C")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .concatWith(Flux.just("0"))
                .onErrorMap((e)->new CustomException(e))
                .retryBackoff(2, Duration.ofSeconds(5));

        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A","B","C")
                .expectNext("A","B","C")
                .expectNext("A","B","C")
                //  .expectError(RuntimeException.class)
                // .verify();
                .expectError(IllegalStateException.class)
                .verify();
    }
}
