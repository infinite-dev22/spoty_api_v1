package io.nomard.spoty_api_v1.controllers.norm;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Brand;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.models.SearchModel;
import io.nomard.spoty_api_v1.services.implementations.BrandServiceImpl;
import com.fasterxml.jackson.annotation.JsonView;
import io.nomard.spoty_api_v1.utils.Views;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("brands")
public class BrandController {
    @Autowired
    private BrandServiceImpl brandService;

    @GetMapping("/all")
    @JsonView(Views.Tiny.class)
    public Page<Brand> getAll(@RequestParam(defaultValue = "0") Integer pageNo,
                              @RequestParam(defaultValue = "50") Integer pageSize) {
        return brandService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public Brand getById(@RequestBody FindModel findModel) throws NotFoundException {
        return brandService.getById(findModel.getId());
    }

    @GetMapping("/search")
    public List<Brand> getByContains(@RequestBody SearchModel searchModel) {
        return brandService.getByContains(searchModel.getSearch());
    }

    @PostMapping("/add")
    public ResponseEntity<ObjectNode> save(@Valid @RequestBody Brand brand) {
        return brandService.save(brand);
    }

    @PutMapping("/update")
    public ResponseEntity<ObjectNode> update(@Valid @RequestBody Brand brand) throws NotFoundException {
        return brandService.update(brand);
    }

    @DeleteMapping("/delete/single")
    public ResponseEntity<ObjectNode> delete(@RequestBody FindModel findModel) {
        return brandService.delete(findModel.getId());
    }

    @DeleteMapping("/delete/multiple")
    public ResponseEntity<ObjectNode> delete(@RequestBody ArrayList<Long> idList) {
        return brandService.deleteMultiple(idList);
    }
}
