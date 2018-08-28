package com.non.dta.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum RuleStatusType implements EnumClass<Integer> {

    INACTIVE(10),
    ACTIVE(20);

    private Integer id;

    RuleStatusType(Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    @Nullable
    public static RuleStatusType fromId(Integer id) {
        for (RuleStatusType at : RuleStatusType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}