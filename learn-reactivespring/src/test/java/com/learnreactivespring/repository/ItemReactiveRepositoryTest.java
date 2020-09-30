package com.learnreactivespring.repository;

import com.learnreactivespring.document.Item;

import org.junit.Before;
//import org.junit.jupiter.api.BeforeEach;
import org.junit.Test;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.BeforeAll;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

@DataMongoTest
@RunWith(SpringRunner.class)
@DirtiesContext
public class ItemReactiveRepositoryTest {

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

List<Item> itemList= Arrays.asList(new Item(null,"Samsung TV",400.0),
        new Item(null,"LG TV",420.0),
        new Item(null,"Apple Watch",299.90),
        new Item("null","Beats Headphones",149.99),
        new Item("ABC","Bose Headphones",250.99));

@Before
public void setUp(){
    itemReactiveRepository.deleteAll()
        .thenMany(Flux.fromIterable(itemList))
        .flatMap(itemReactiveRepository::save)
    .doOnNext((item->{
        System.out.println("Inserted Item is:"+item);
    })).blockLast();
}

    @Test
    public void getAllItems(){
    StepVerifier.create(itemReactiveRepository.findAll())
    .expectSubscription()
    .expectNextCount(5)
    .verifyComplete();
}

    @Test
    public void getItemById(){
    StepVerifier.create(itemReactiveRepository.findById("ABC"))
        .expectSubscription()
        .expectNextMatches((item->item.getDescription().equals("Bose Headphones")))
        .verifyComplete();

}

    @Test
    public void findItemByDescription(){
    StepVerifier.create(itemReactiveRepository
            .findByDescription("LG TV").log("findItemByDescription"))
            .expectSubscription()
            .expectNextCount(1)
            .verifyComplete();
}

    @Test
    public void saveItem(){
    Item item=new Item(null,"Google Home Mini",30.00);
        Mono<Item> savedItem = itemReactiveRepository.save(item);

        StepVerifier.create(savedItem.log("savedItem: "))
                .expectSubscription()
                .expectNextMatches(item1->(item1.getId()!=null && item1.getDescription().equals("Google Home Mini")))
                .verifyComplete();



    }

    @Test
    public void updateItem(){
    double newPrice=520.00;
       // Flux<Item> updatedItem = itemReactiveRepository.findByDescription("Beats Headphones")
        Mono<Item> updatedItem = itemReactiveRepository.findByDescription("Beats Headphones")
                .map(item -> {
                    item.setPrice(newPrice); //setting the new price
                    return item;
                })
                .flatMap(item -> itemReactiveRepository.save(item));//save the item with new price

        StepVerifier.create(updatedItem.log("updateItem"))
                .expectSubscription()
                .expectNextMatches(item->item.getPrice()==520.00)
                .verifyComplete();

}


@Test
    public void deleteItemById(){
    Mono<Void> deletedItem = itemReactiveRepository.findById("ABC")//Mono<Item>
            .map(Item::getId)//getId-> Transform from one type to another type
            .flatMap((id) -> {
                return itemReactiveRepository.deleteById(id);
            });

    StepVerifier.create(deletedItem.log())
            .expectSubscription()
            .verifyComplete();

    StepVerifier.create(itemReactiveRepository.findAll().log("New Item List"))
            .expectSubscription()
            .expectNextCount(4)
            .verifyComplete();

}

    @Test
    public void deleteItem(){
        Mono<Void> deletedItem = itemReactiveRepository.findByDescription("Bose Headphones")//Mono<Item>
                .flatMap((item) -> {
                    return itemReactiveRepository.delete(item);
                });

        StepVerifier.create(deletedItem.log())
                .expectSubscription()
                .verifyComplete();

        StepVerifier.create(itemReactiveRepository.findAll().log("New Item List"))
                .expectSubscription()
                .expectNextCount(4)
                .verifyComplete();

    }
}
