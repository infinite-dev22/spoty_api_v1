package io.nomard.spoty_api_v1.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Brand;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.models.SearchModel;
import io.nomard.spoty_api_v1.services.implementations.BrandServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;

@RestController
@RequestMapping("brands")
public class BrandController {
    @Autowired
    private BrandServiceImpl brandService;

    @GetMapping("/all")
    public Flux<PageImpl<Brand>> getAll(@RequestParam(defaultValue = "0") Integer pageNo,
                                        @RequestParam(defaultValue = "50") Integer pageSize) {
        return brandService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public Mono<Brand> getById(@RequestBody FindModel findModel) {
        return brandService.getById(findModel.getId());
    }

    @GetMapping("/search")
    public Flux<Brand> getByContains(@RequestBody SearchModel searchModel) {
        return brandService.getByContains(searchModel.getSearch());
    }

    @PostMapping("/add")
    public Mono<ResponseEntity<ObjectNode>> save(@Valid @RequestBody Brand brand) {
        return brandService.save(brand);
    }

    @PutMapping("/update")
    public Mono<ResponseEntity<ObjectNode>> update(@Valid @RequestBody Brand brand) {
        return brandService.update(brand);
    }

    @DeleteMapping("/delete/single")
    public Mono<ResponseEntity<ObjectNode>> delete(@RequestBody FindModel findModel) {
        return brandService.delete(findModel.getId());
    }

    @DeleteMapping("/delete/multiple")
    public Mono<ResponseEntity<ObjectNode>> delete(@RequestBody ArrayList<Long> idList) {
        return brandService.deleteMultiple(idList);
    }
}
