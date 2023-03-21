package com.project.challenge.application.adapter;

import com.project.challenge.domain.entity.History;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface HistoryQueryService {

    Page<History> findAllPaged(Pageable pageable);


}
