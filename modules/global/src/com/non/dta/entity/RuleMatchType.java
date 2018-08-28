package com.non.dta.entity;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum RuleMatchType implements EnumClass<Integer> {

    EXACT_MATCH(10);

    private Integer id;

    RuleMatchType(Integer value) {
        this.id = value;
    }

    public Integer getId() {
        return id;
    }

    @Nullable
    public static RuleMatchType fromId(Integer id) {
        for (RuleMatchType at : RuleMatchType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}