package com.laundry.version_one.machine;

import com.laundry.version_one.store.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import javax.crypto.Mac;

public interface MachineRepository extends JpaRepository<Machine,Integer>, JpaSpecificationExecutor<Machine> {
    @Query("""
            select m
            from Machine m
            where m.store.id = :storeId
""")
    Page<Machine> findAllMachinesInStore(Pageable pageable, Integer storeId);
}
