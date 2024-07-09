package com.laundry.version_one.machine;

import com.laundry.version_one.booking.Booking;
import com.laundry.version_one.store.Store;
import jakarta.persistence.*;
import jdk.dynalink.linker.LinkerServices;
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
public class Machine {
    @Id
    @GeneratedValue
    private Integer id;
    private Double averageCostPerHour;
    private Boolean isOn;
    private String type;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDate createdDate;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;

    @OneToMany(mappedBy = "machine")
    private List<Booking> bookings;
}
