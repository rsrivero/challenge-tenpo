package com.project.challenge.infrastructure.rest.controller;

import com.project.challenge.application.usecases.SumOperationService;
import com.project.challenge.infrastructure.rest.request.OperationDTORequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/operation")
public class OperationController {

    @Autowired
    private SumOperationService sumOperationService;

    @GetMapping("/sum")
    public ResponseEntity<Double> calculate(@Valid  @RequestBody OperationDTORequest req){
        return ResponseEntity.ok(sumOperationService.apply(req));
    }

}
