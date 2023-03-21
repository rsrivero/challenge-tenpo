package com.project.challenge.infrastructure.rest.controller;

import com.project.challenge.application.usecases.ListHistoryService;
import com.project.challenge.domain.entity.History;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/history")
public class HistoryController {

    @Autowired
    private ListHistoryService listHistoryService;

    @GetMapping
    public Page<History> getHistory(Pageable page){
        return listHistoryService.apply(page);
    }

}
