package com.laundry.version_one.store;

import com.laundry.version_one.machine.Machine;
import com.laundry.version_one.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Store {
    @Id
    @GeneratedValue
    private Integer id;

    @Column(unique = true)
    private String name;
    private String address;
//  private String location;
    private Double storeRevenue;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDate createdDate;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToMany(mappedBy = "store")
    private List<Machine> machines;

    @Transient
    public List<Integer> getMachineIds(){
        if(machines==null || machines.isEmpty()){
            return null;
        }
        return this.machines.stream().map(Machine::getId).toList();
    }
}
