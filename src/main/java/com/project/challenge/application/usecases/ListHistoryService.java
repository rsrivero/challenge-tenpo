package com.project.challenge.application.usecases;

import com.project.challenge.application.adapter.HistoryQueryService;
import com.project.challenge.domain.entity.History;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ListHistoryService {

    @Autowired
    private HistoryQueryService historyQueryService;

    public Page<History> apply(Pageable page) {
        return historyQueryService.findAllPaged(page);
    }
}
