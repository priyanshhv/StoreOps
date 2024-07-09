package com.laundry.version_one.machine;

import com.laundry.version_one.common.PageResponse;
import com.laundry.version_one.exception.OperationNotPermittedException;
import com.laundry.version_one.role.RoleRepository;
import com.laundry.version_one.store.Store;
import com.laundry.version_one.store.StoreRepository;
import com.laundry.version_one.store.StoreResponse;
import com.laundry.version_one.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MachineService {
    private final RoleRepository roleRepository;
    private final MachineMapper machineMapper;
    private final MachineRepository machineRepository;
    private final StoreRepository storeRepository;

    @Transactional
    public Integer save(MachineRequest request, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        boolean hasOwnerRole = user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("OWNER"));
        if(!hasOwnerRole)
        {
            throw new OperationNotPermittedException("You doesn't own a store");
        }

//        System.out.println("\n\n\n\n\n");
//        List<Store> storess =  user.getStores();
//        Integer siz = storess.size();
//        user.getStores().forEach(store -> {
//            System.out.println(store.getName());
//        });
//        storess.forEach(store -> {
//            System.out.println(store.getName());
//        });
//       System.out.println("\n\n\n\n\n");

        Optional<List<Store>> stores = Optional.ofNullable(storeRepository.findAllStoresByOwnerId(user.getId()));
        if(stores.isEmpty()){
            throw new OperationNotPermittedException("You doesn't own a store");
        }

        Integer storeId = stores.get().get(0).getId();

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new EntityNotFoundException("No store found with ID: " + storeId));

        Machine machine = machineMapper.toMachine(request);
        machine.setStore(store);

        machineRepository.save(machine); // Save the machine once

        return machine.getId(); // Return the saved machine's ID

    }

    @Transactional
    public Integer save(Integer storeId,MachineRequest request, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        boolean hasOwnerRole = user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("OWNER"));

        if(!hasOwnerRole)
        {
            throw new OperationNotPermittedException("You doesn't own a store");
        }
        Store store = storeRepository.findById(storeId).orElseThrow(() -> new EntityNotFoundException("No store found with ID:: " + storeId));
        if(!Objects.equals(store.getOwner().getId(), user.getId())){
            throw new OperationNotPermittedException("You doesn't own this "+store.getName()+" store");
        }
        Machine machine = machineMapper.toMachine(request);
        machine.setStore(store);
        machineRepository.save(machine);
        return machineRepository.save(machine).getId();
    }

    public PageResponse<MachineResponse> findAllMachinesInStore(Integer storeId, int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());

        Store store = storeRepository.findById(storeId).orElseThrow(() -> new EntityNotFoundException("No store found with ID:: " + storeId));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());

        Page<Machine> machines =  machineRepository.findAllMachinesInStore(pageable,storeId);
        List<MachineResponse> machineResponse = machines.stream().map(machineMapper::toMachineResponse).toList();
        return new PageResponse<>(
                machineResponse,
                machines.getNumber(),
                machines.getSize(),
                machines.getTotalElements(),
                machines.getTotalPages(),
                machines.isFirst(),
                machines.isLast()
        );
    }
}
