package com.hrms.common.entity;

import jakarta.persistence.EntityManager;
import org.springframework.transaction.annotation.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SoftDeleteTest {

    @Autowired
    private TestItemRepository repo;

    @Autowired
    private EntityManager em;

    @Test
    void softDelete_excludesFromStandardQuery() {
        TestItem item = repo.saveAndFlush(new TestItem("to-be-soft-deleted"));
        Long id = item.getId();

        item.setActive(false);
        repo.saveAndFlush(item);
        em.clear();

        List<TestItem> all = repo.findAll();
        assertThat(all).noneMatch(i -> i.getId().equals(id));
    }

    @Test
    void softDelete_recordStillExistsInDatabase() {
        TestItem item = repo.saveAndFlush(new TestItem("ghost-record"));
        Long id = item.getId();

        item.setActive(false);
        repo.saveAndFlush(item);
        em.clear();

        // standard findById returns empty (filtered by @SQLRestriction)
        assertThat(repo.findById(id)).isEmpty();

        // native query bypasses @SQLRestriction — record must still be there
        TestItem ghost = repo.findByIdNative(id).orElseThrow();
        assertThat(ghost.getActive()).isFalse();
        assertThat(ghost.getName()).isEqualTo("ghost-record");
    }

    @Test
    void activeRecord_visibleInStandardQuery() {
        TestItem item = repo.saveAndFlush(new TestItem("active-record"));
        em.clear();

        assertThat(repo.findById(item.getId())).isPresent();
        assertThat(repo.findAll()).anyMatch(i -> i.getId().equals(item.getId()));
    }
}
