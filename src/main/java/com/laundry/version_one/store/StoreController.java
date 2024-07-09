package com.laundry.version_one.store;

import com.laundry.version_one.common.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("store")
@RequiredArgsConstructor
@Tag(name = "Store")
public class StoreController {
    private final StoreService service;

    //save the store and if u r opening first time then set the role as owner also
    @PostMapping
    public ResponseEntity<Integer> saveStore(
            @Valid @RequestBody StoreRequest request,
            Authentication connectedUser
    ){
        return ResponseEntity.ok(service.save(request,connectedUser));
    }

    //get all stores but if u are owner then only show other's stores
    @GetMapping
    public ResponseEntity<PageResponse<StoreResponse>> findStores(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser){
        return ResponseEntity.ok(service.findAllStores(page,size,connectedUser));
    }
}
