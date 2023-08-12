package com.example.controller;

import com.example.entity.CommonObject;
import com.example.service.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/common/")
public class CommonController {

    @Autowired
    CommonService commonService;

    @PostMapping("add")
    public ResponseEntity addDataToMongo(@RequestBody CommonObject commonObject){
            commonService.add(commonObject);
            return ResponseEntity.ok("Object saved");
    }
        @PostMapping("addObject")
    public ResponseEntity addObjectToMongo(@RequestBody Object object){
            commonService.addObject(object);
            return ResponseEntity.ok("Object saved");
    }
        @GetMapping("getProductByMinRating")
    public ResponseEntity getProductByMinRating(@RequestParam Object minRat){
            return ResponseEntity.ok(commonService.findProductsByRatingGreaterThan(minRat));
    }

}
