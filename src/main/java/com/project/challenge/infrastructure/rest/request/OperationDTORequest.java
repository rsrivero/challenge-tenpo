package com.project.challenge.infrastructure.rest.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OperationDTORequest {

    @NotNull
    private Double first;

    @NotNull
    private Double second;
}
