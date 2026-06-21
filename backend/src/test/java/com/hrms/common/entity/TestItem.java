package com.hrms.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "test_items")
@Getter
@Setter
public class TestItem extends BaseEntity {

    @Column
    private String name;

    public TestItem(String name) {
        this.name = name;
    }

    public TestItem() {}
}
