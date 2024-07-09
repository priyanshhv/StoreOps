package com.laundry.version_one.store;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StoreRepository extends JpaRepository<Store,Integer> , JpaSpecificationExecutor<Store> {
    @Query("""
            select s
            from Store s
            where s.owner.id != :userId
""")
    Page<Store> findAllDisplayableStores(Pageable pageable, Integer userId);

    @Query("""
        select s
        from Store s
        where s.owner.id = :userId
""")
    List<Store> findAllStoresByOwnerId(Integer userId);
}


