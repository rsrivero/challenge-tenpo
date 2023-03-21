package com.project.challenge.infrastructure.persistence.repository;

import com.project.challenge.application.adapter.HistoryCommandService;
import com.project.challenge.application.adapter.HistoryQueryService;
import com.project.challenge.domain.entity.History;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoryRepository extends JpaRepository<History, Integer>,
        JpaSpecificationExecutor<History>,
        HistoryCommandService,
        HistoryQueryService {


    default Page<History> findAllPaged(Pageable pageable) {
        return this.findAll(pageable);
    }

}
