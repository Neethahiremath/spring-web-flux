package com.learnreactivespring.controller;

import com.learnreactivespring.document.Item;
import org.junit.Test;
//import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureWebTestClient
@DirtiesContext
public class FluxAndMonoControllerTest {
@Autowired
    WebTestClient webTestClient;

    @Test
    public void fluxApproach1(){
    Flux<Integer> stringFlux=webTestClient.get().uri("/flux")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .returnResult(Integer.class)
            .getResponseBody();

    StepVerifier.create(stringFlux)
            .expectSubscription()
            .expectNext(1)
            .expectNext(2)
            .expectNext(3)
            .expectNext(4)
            .verifyComplete();

}

@Test
    public void flux_approach2(){
    webTestClient.get().uri("/flux")
            .accept(MediaType.APPLICATION_JSON_UTF8)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
            .expectBodyList(Integer.class)
            .hasSize(4);



}

@Test
    public void fluxApproach3(){
    List<Integer> expectedIntList= Arrays.asList(1,2,3,4);
    final EntityExchangeResult<List<Integer>> listEntityExchangeResult = webTestClient.get().uri("/flux")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
            .expectBodyList(Integer.class)
            .returnResult();
    assertEquals(expectedIntList,listEntityExchangeResult.getResponseBody());


}

    @Test
    public void fluxApproach4(){
        List<Integer> expectedIntList= Arrays.asList(1,2,3,4);
       webTestClient.get().uri("/flux")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBodyList(Integer.class)
               .consumeWith((response)->{
                   assertEquals(expectedIntList,response.getResponseBody());
               });


    }

    @Test
    public void fluxStream(){

        Flux<Long> longstreamFlux=webTestClient.get().uri("/fluxinfinitestream")
                .accept(MediaType.APPLICATION_STREAM_JSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(Long.class)
                .getResponseBody();

        StepVerifier.create(longstreamFlux)
                .expectNext(0L,1L,2L)
                .thenCancel()
                .verify();



    }

    @Test
    public void monoTest(){
    Integer expectedValue=new Integer(1);
    webTestClient.get().uri("/mono")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBody(Integer.class)
            .consumeWith((res)->{
                assertEquals(expectedValue,res.getResponseBody());
            });
    }



}
