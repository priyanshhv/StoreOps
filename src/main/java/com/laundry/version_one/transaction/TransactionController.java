package com.laundry.version_one.transaction;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("transaction")
@RequiredArgsConstructor
@Tag(name = "Transaction")
public class TransactionController {
    private final TransactionService service;

    @PostMapping("/open")
    public ResponseEntity<Integer> saveTransaction(
           @Valid @RequestBody TransactionRequest request,
           Authentication connectedUser
    ){
        return ResponseEntity.ok(service.save(request,connectedUser));
    }

    @PatchMapping("/close")
    public ResponseEntity<Integer> closeTransaction(
            Authentication connectedUser,
            @Valid @RequestBody TransactionRequest request
    ){
        return ResponseEntity.ok(service.close(request,connectedUser));
    }

}
