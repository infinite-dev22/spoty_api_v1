package io.nomard.spoty_api_v1.controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.nomard.spoty_api_v1.entities.Attendance;
import io.nomard.spoty_api_v1.errors.NotFoundException;
import io.nomard.spoty_api_v1.models.FindModel;
import io.nomard.spoty_api_v1.services.implementations.AttendanceServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("attendances")
public class AttendanceController {
    @Autowired
    private AttendanceServiceImpl attendanceService;


    @GetMapping("/all")
    public List<Attendance> getAll(@RequestParam(defaultValue = "0") Integer pageNo,
                                   @RequestParam(defaultValue = "50") Integer pageSize) {
        return attendanceService.getAll(pageNo, pageSize);
    }

    @GetMapping("/single")
    public Attendance getById(@RequestBody FindModel findModel) throws NotFoundException {
        return attendanceService.getById(findModel.getId());
    }

//    @GetMapping("/search")
//    public List<Attendance> getByContains(@RequestBody SearchModel searchModel) {
//        return attendanceService.getByContains(searchModel.getSearch());
//    }

    @PostMapping("/add")
    public ResponseEntity<ObjectNode> save(@Valid @RequestBody Attendance attendance) {
        return attendanceService.save(attendance);
    }

    @PutMapping("/update")
    public ResponseEntity<ObjectNode> update(@Valid @RequestBody Attendance attendance) throws NotFoundException {
        return attendanceService.update(attendance);
    }

    @DeleteMapping("/single/delete")
    public ResponseEntity<ObjectNode> delete(@RequestBody FindModel findModel) {
        return attendanceService.delete(findModel.getId());
    }
}
