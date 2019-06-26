package com.total.unique.generator.repository;

import com.total.unique.generator.entity.UniqueID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UniqueIDRepository extends JpaRepository<UniqueID, Long> {

    List<UniqueID> findByStatus(UniqueID.Status status, Pageable pageable);
}
