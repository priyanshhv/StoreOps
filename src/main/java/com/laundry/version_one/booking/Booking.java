package com.laundry.version_one.booking;

import com.laundry.version_one.machine.Machine;
import com.laundry.version_one.transaction.Transaction;
import com.laundry.version_one.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Booking {
    @Id
    @GeneratedValue
    private Integer id;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDate createdDate;

    @Column(nullable = false, updatable = false)
    private LocalTime startTime;
    @Column(nullable = false, updatable = false)
    private LocalTime endTime;
    private Double totalAmount;

    @ManyToOne
    @JoinColumn(name = "machine_id")
    private Machine machine;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @OneToMany(mappedBy = "booking")
    private List<Transaction> transactions;
}
