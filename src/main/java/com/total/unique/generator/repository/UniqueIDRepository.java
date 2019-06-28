package com.total.unique.generator.repository;

import com.total.unique.generator.entity.UniqueID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import java.util.List;

@Repository
public interface UniqueIDRepository extends JpaRepository<UniqueID, Long> {

    List<UniqueID> findByStatus(UniqueID.Status status, Pageable pageable);

    // Mandatory the FOR UPDATE (pessimistic_write) to lock row from reading
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(value = "select u.uniqueId from UniqueID u where status <> :status")
    List<String> findUniqueIdsNotInStatus(UniqueID.Status status, Pageable pageable);
}
