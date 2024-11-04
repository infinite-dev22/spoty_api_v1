package io.nomard.spoty_api_v1.controllers.norm;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.deductions.Discount;
import io.nomard.spoty_api_v1.utils.json_mapper.dto.DiscountDTO;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.services.implementations.deductions.DiscountServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@RequestMapping("discounts")
public class DiscountController {
    @Autowired
    private DiscountServiceImpl discountService;

    @GetMapping("/all")
    public Page<DiscountDTO> getAll(@RequestParam(defaultValue = "0") Integer pageNo,
                                    @RequestParam(defaultValue = "50") Integer pageSize) {
        return discountService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public DiscountDTO getById(@RequestBody FindModel findModel) throws NotFoundException {
        return discountService.getById(findModel.getId());
    }

    @PostMapping("/add")
    public ResponseEntity<ObjectNode> save(@Valid @RequestBody Discount discount) {
        return discountService.save(discount);
    }

    @PutMapping("/update")
    public ResponseEntity<ObjectNode> update(@Valid @RequestBody Discount discount) throws NotFoundException {
        return discountService.update(discount);
    }

    @DeleteMapping("/delete/single")
    public ResponseEntity<ObjectNode> delete(@RequestBody FindModel findModel) {
        return discountService.delete(findModel.getId());
    }

    @DeleteMapping("/delete/multiple")
    public ResponseEntity<ObjectNode> deleteMultiple(@RequestBody ArrayList<Long> ids) throws NotFoundException {
        return discountService.deleteMultiple(ids);
    }
}
