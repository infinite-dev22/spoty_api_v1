package io.nomard.spoty_api_v1.controllers.norm;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.utils.json_mapper.dto.EmployeeDTO;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.models.SearchModel;
import io.nomard.spoty_api_v1.models.UserModel;
import io.nomard.spoty_api_v1.services.implementations.EmployeeServiceImpl;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employees")
public class EmployeeController {
    @Autowired
    private EmployeeServiceImpl userService;

    @GetMapping("/all")
    public Page<EmployeeDTO.EmployeeAsWholeDTO> getAll(@RequestParam(defaultValue = "0") Integer pageNo,
                                                       @RequestParam(defaultValue = "50") Integer pageSize) {
        return userService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public EmployeeDTO.EmployeeAsWholeDTO getById(@RequestBody FindModel findModel) throws NotFoundException {
        return userService.getById(findModel.getId());
    }

    @GetMapping("/search")
    public List<EmployeeDTO.EmployeeAsWholeDTO> getByContains(@RequestBody SearchModel searchModel) {
        return userService.getByContains(searchModel.getSearch());
    }

    @PostMapping("/add")
    public ObjectNode save(@Valid @RequestBody UserModel userModel) throws NotFoundException, MessagingException {
        return userService.add(userModel).getBody();
    }

    @PutMapping("/update")
    public ResponseEntity<ObjectNode> update(@Valid @RequestBody UserModel userModel) throws NotFoundException {
        return userService.update(userModel);
    }

    @DeleteMapping("/delete/single")
    public ResponseEntity<ObjectNode> delete(@RequestBody FindModel findModel) {
        return userService.delete(findModel.getId());
    }
}
