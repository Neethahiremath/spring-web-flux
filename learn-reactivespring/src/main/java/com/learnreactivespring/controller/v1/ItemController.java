package com.learnreactivespring.controller.v1;

import com.learnreactivespring.document.Item;
import com.learnreactivespring.repository.ItemReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.xml.ws.Response;

import static com.learnreactivespring.constants.ItemConstants.ITEM_END_POINT_V1;

@RestController
@Slf4j
public class ItemController {

  /*  @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException re){
        log.error("Exception Caught is handleRuntimeException: {}",re);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(re.getMessage());
    }*/

   // @Autowired
    ItemReactiveRepository itemReactiveRepository;

    public ItemController(ItemReactiveRepository itemReactiveRepository ){
        this.itemReactiveRepository=itemReactiveRepository;
    }



    @GetMapping(value = ITEM_END_POINT_V1)//,produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<Item> getAllItems(){

        return itemReactiveRepository.findAll().log();
    }

    @GetMapping(path=ITEM_END_POINT_V1+"/{id}")
    public Mono<ResponseEntity<Item>> getOneItem(@PathVariable String id){
        return itemReactiveRepository.findById(id).map(item->new ResponseEntity<>(item, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));

    }

    @PostMapping(ITEM_END_POINT_V1)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Item> createItem(@RequestBody Item item){
        return itemReactiveRepository.save(item);

    }


    @DeleteMapping(ITEM_END_POINT_V1+"/{id}")
    public Mono<Void> deleteItem(@PathVariable String id){
        return itemReactiveRepository.deleteById(id);

    }


    @GetMapping(ITEM_END_POINT_V1+"/runtimeException")
    public Flux<Item> runtimeException(){
        return itemReactiveRepository.findAll()
                .concatWith(Mono
                        .error(new RuntimeException("RunTimeException Occurred")));

    }


    //id and item to be updated is the req=path variable and request body -completed
    //using the id get the item from the database -completed
    //updated the item retrieved with the value from the request body -completed
    //save the saved item.

    @PutMapping(ITEM_END_POINT_V1+"/{id}")
    public Mono<ResponseEntity<Item>> updateItem(@PathVariable String id, @RequestBody Item item){

    return    itemReactiveRepository.findById(id)
            .flatMap(currentItem->{
                    currentItem.setPrice(item.getPrice());
                    currentItem.setDescription(item.getDescription());
                    return itemReactiveRepository.save(currentItem);
                })
            .map(updateItem-> new ResponseEntity<>(updateItem,HttpStatus.OK))
        .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));


    }
}
