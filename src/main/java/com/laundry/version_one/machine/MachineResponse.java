package com.laundry.version_one.machine;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MachineResponse {
    private Integer id;
    private Double averageCostPerHour;
    private Boolean isOn;
    private String type;
}
