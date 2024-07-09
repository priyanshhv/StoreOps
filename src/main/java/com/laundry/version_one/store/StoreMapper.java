package com.laundry.version_one.store;

import com.laundry.version_one.machine.Machine;
import com.laundry.version_one.machine.MachineRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StoreMapper {
    public Store toStore(StoreRequest request){
        Store store = new Store();
        store.setName(request.name());
        store.setAddress(request.address());
        store.setStoreRevenue(0.0);
        return store;
    }

    public StoreResponse toStoreResponse(Store request){
        StoreResponse storeResponse = new StoreResponse();
        storeResponse.setId(request.getId());
        storeResponse.setName(request.getName());
        storeResponse.setAddress(request.getAddress());
        storeResponse.setOwnerName(request.getOwner().getFullName());
        storeResponse.setMachineIds(request.getMachineIds());
        return storeResponse;
    }
}
