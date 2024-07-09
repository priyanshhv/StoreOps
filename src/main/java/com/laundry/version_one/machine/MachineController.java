package com.laundry.version_one.machine;
import com.laundry.version_one.common.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("machine")
@RequiredArgsConstructor
@Tag(name = "Machine")
public class MachineController {
    private final MachineService service;

    //save the machine if u r the store owner
    @PostMapping
    public ResponseEntity<Integer> saveMachine(
            @Valid @RequestBody MachineRequest request,
            Authentication connectedUser
    ){
        return ResponseEntity.ok(service.save(request,connectedUser));
    }

    //save the machine if u r the store owner and you really own that store
    @PostMapping("/{store-id}")
    public ResponseEntity<Integer> findBookById(
            @PathVariable("store-id") Integer storeId,
            @Valid @RequestBody MachineRequest request,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(service.save(storeId,request,connectedUser));
    }

    //fetch all the machines in the store if store exists
    @GetMapping("/{store-id}")
    public ResponseEntity<PageResponse<MachineResponse>> findMachinesInStore(
            @PathVariable("store-id") Integer storeId,
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser){
        return ResponseEntity.ok(service.findAllMachinesInStore(storeId,page,size,connectedUser));
    }
}


