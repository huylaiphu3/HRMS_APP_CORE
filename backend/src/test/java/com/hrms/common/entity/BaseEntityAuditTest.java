package com.hrms.common.entity;

import com.hrms.common.security.CustomUserDetails;
import jakarta.persistence.EntityManager;
import org.springframework.transaction.annotation.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BaseEntityAuditTest {

    @Autowired
    private TestItemRepository repo;

    @Autowired
    private EntityManager em;

    @BeforeEach
    void setUpSecurityContext() {
        var authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        var userDetails = new CustomUserDetails(42L, "ADMIN", null, "admin", "", authorities);
        var auth = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void persist_setsIdAndActiveTrue() {
        TestItem item = repo.saveAndFlush(new TestItem("test"));
        assertThat(item.getId()).isNotNull().isPositive();
        assertThat(item.getActive()).isTrue();
    }

    @Test
    void persist_setsCreatedAtAndUpdatedAt() {
        TestItem item = repo.saveAndFlush(new TestItem("audit-test"));
        em.clear();
        TestItem reloaded = repo.findById(item.getId()).orElseThrow();
        assertThat(reloaded.getCreatedAt()).isNotNull();
        assertThat(reloaded.getUpdatedAt()).isNotNull();
    }

    @Test
    void persist_setsCreatedByFromSecurityContext() {
        TestItem item = repo.saveAndFlush(new TestItem("auditor-test"));
        em.clear();
        TestItem reloaded = repo.findById(item.getId()).orElseThrow();
        assertThat(reloaded.getCreatedBy()).isEqualTo(42L);
    }

    @Test
    void persist_createdAtNotUpdatableAfterUpdate() {
        TestItem item = repo.saveAndFlush(new TestItem("immutable-created-at"));
        em.clear();
        TestItem reloaded = repo.findById(item.getId()).orElseThrow();
        var originalCreatedAt = reloaded.getCreatedAt();

        reloaded.setName("updated name");
        repo.saveAndFlush(reloaded);
        em.clear();

        TestItem updated = repo.findById(item.getId()).orElseThrow();
        assertThat(updated.getCreatedAt()).isEqualTo(originalCreatedAt);
    }
}
