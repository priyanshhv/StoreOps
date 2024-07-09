package com.laundry.version_one.transaction;

import com.laundry.version_one.booking.Booking;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Transaction {
    @Id
    @GeneratedValue
    private Integer id;

    private Double amount;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDate createdDate;

    @Column(nullable = false, updatable = false)
    private LocalTime startTime;

    @Column(insertable = false)
    private LocalTime endTime;

    @ManyToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;
}
