package com.laundry.version_one.transaction;

import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Service
public class TransactionMapper {
    public Transaction toTransaction(TransactionRequest request) {
        Transaction transaction = new Transaction();
        transaction.setStartTime(LocalTime.now());
        return transaction;
    }
}
