package com.nilecare.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModuleDistributionDTO {
    private Integer completed;
    private Integer inProgress;
    private Integer notStarted;
}
