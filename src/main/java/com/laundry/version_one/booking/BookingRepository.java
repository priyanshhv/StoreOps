package com.laundry.version_one.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer>, JpaSpecificationExecutor<Booking> {
    @Query("""
        select b
        from Booking b
        where b.machine.id = :machineId
        and b.createdDate = :todaysDate
        and b.startTime > :currentTime
        order by b.startTime
""")
    List<Booking> findAllBookingsHavingMachineIdCreatedTodayAfterCurrentTime(Integer machineId, LocalDate todaysDate, LocalTime currentTime);

    @Query("""
        select b
        from Booking b
        where b.machine.id = :machineId
        and b.createdDate = :todaysDate
        order by b.startTime
""")
    List<Booking> findAllBookingByCurrentDate(Integer machineId,LocalDate todaysDate);

    @Query("""
        select b 
        from Booking b 
        where b.machine.id = :machineId
        order by b.createdDate desc 
""")
    Page<Booking> findAllByMachineId(Pageable pageable, Integer machineId);
}
