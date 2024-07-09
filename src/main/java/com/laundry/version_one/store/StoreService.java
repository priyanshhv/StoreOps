package com.laundry.version_one.store;

import com.laundry.version_one.common.PageResponse;
import com.laundry.version_one.role.Role;
import com.laundry.version_one.role.RoleRepository;
import com.laundry.version_one.user.User;
import com.laundry.version_one.user.UserRepository;
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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreMapper storeMapper;
    private final StoreRepository storeRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @Transactional
    public Integer save(StoreRequest request, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Store store = storeMapper.toStore(request);
        store.setOwner(user);
        boolean hasOwnerRole = user.getRoles().stream()
                .anyMatch(role -> role.getName().equals("OWNER"));
        if(!hasOwnerRole){
            Role role = roleRepository.findByName("OWNER").orElseThrow(() -> new EntityNotFoundException("No Role found while opening a store and making the user as Owner"));
            user.getRoles().add(role);
        }
        userRepository.save(user);
        return storeRepository.save(store).getId();
    }

    public PageResponse<StoreResponse> findAllStores(int page, int size, Authentication connectedUser) {
        User user = ((User) connectedUser.getPrincipal());
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Store> stores = storeRepository.findAllDisplayableStores(pageable,user.getId());
        List<StoreResponse> storesResponse = stores.stream().map(storeMapper::toStoreResponse).toList();
        return new PageResponse<>(
                storesResponse,
                stores.getNumber(),
                stores.getSize(),
                stores.getTotalElements(),
                stores.getTotalPages(),
                stores.isFirst(),
                stores.isLast()
        );
    }
}
