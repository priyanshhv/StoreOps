package com.laundry.version_one.transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction,Integer>, JpaSpecificationExecutor<Transaction> {
    List<Transaction> findAllByBookingId(Integer id);

    @Query("""
        select t from Transaction t
        where t.booking.id = :bookingId
        and
        t.createdDate = :currentDate
        order by t.id desc
        limit 1
""")
    Transaction findByBookingId(Integer bookingId, LocalDate currentDate);
}
