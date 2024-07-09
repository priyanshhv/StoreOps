package com.laundry.version_one.store;

import com.laundry.version_one.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StoreResponse {
    private Integer id;
    private String name;
    private String address;
    private String ownerName;
    private List<Integer> machineIds;
}
