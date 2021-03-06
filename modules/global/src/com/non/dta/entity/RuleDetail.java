package com.non.dta.entity;

import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@NamePattern("%s %s %s|baseRecordField,matchingRecordField,matchType")
@Table(name = "NONDTA_RULE_DETAIL")
@Entity(name = "nondta$RuleDetail")
public class RuleDetail extends StandardEntity {
    private static final long serialVersionUID = 3815590654277496697L;

    @NotNull
    @Column(name = "BASE_RECORD_FIELD", nullable = false)
    protected String baseRecordField;

    @NotNull
    @Column(name = "MATCHING_RECORD_FIELD", nullable = false)
    protected String matchingRecordField;

    @NotNull
    @Column(name = "MATCH_TYPE", nullable = false)
    protected Integer matchType;

    @Column(name = "USE_ID")
    protected Boolean useId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RULE_ID")
    protected Rule rule;

    public void setUseId(Boolean useId) {
        this.useId = useId;
    }

    public Boolean getUseId() {
        return useId;
    }


    public void setRule(Rule rule) {
        this.rule = rule;
    }

    public Rule getRule() {
        return rule;
    }

    public void setMatchType(RuleMatchType matchType) {
        this.matchType = matchType == null ? null : matchType.getId();
    }

    public RuleMatchType getMatchType() {
        return matchType == null ? null : RuleMatchType.fromId(matchType);
    }

    public void setBaseRecordField(String baseRecordField) {
        this.baseRecordField = baseRecordField;
    }

    public String getBaseRecordField() {
        return baseRecordField;
    }

    public void setMatchingRecordField(String matchingRecordField) {
        this.matchingRecordField = matchingRecordField;
    }

    public String getMatchingRecordField() {
        return matchingRecordField;
    }

    @Override
    public String toString() {
        return super.toString() + "[Matching Field: " + this.getMatchingRecordField() + "] [Base Field: " + this.getBaseRecordField() + "] [Match Type: " + this.getMatchType() + "]";
    }
}