package com.hrms.common.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TestItemRepository extends JpaRepository<TestItem, Long> {

    @Query(value = "SELECT * FROM test_items WHERE id = :id", nativeQuery = true)
    Optional<TestItem> findByIdNative(@Param("id") Long id);
}
