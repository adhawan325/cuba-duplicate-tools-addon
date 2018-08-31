package com.non.dta.entity;

import com.haulmont.chile.core.annotations.Composition;
import com.haulmont.chile.core.annotations.NamePattern;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.entity.annotation.OnDelete;
import com.haulmont.cuba.core.global.DeletePolicy;

import javax.persistence.*;
import java.util.List;

@NamePattern("%s %s %s|baseRecordType,matchingRecordType,status")
@Table(name = "NONDTA_RULE")
@Entity(name = "nondta$Rule")
public class Rule extends StandardEntity {
    private static final long serialVersionUID = -7408722613119771537L;

    @Column(name = "NAME")
    protected String name;

    @Column(name = "STATUS")
    protected Integer status;

    @Lob
    @Column(name = "DESCRIPTION")
    protected String description;

    @Column(name = "BASE_RECORD_TYPE")
    protected String baseRecordType;

    @Column(name = "MATCHING_RECORD_TYPE")
    protected String matchingRecordType;

    @Column(name = "CASE_SENSITIVE")
    protected Boolean caseSensitive;

    @Composition
    @OnDelete(DeletePolicy.CASCADE)
    @OneToMany(mappedBy = "rule")
    protected List<RuleDetail> ruleDetail;

    public void setRuleDetail(List<RuleDetail> ruleDetail) {
        this.ruleDetail = ruleDetail;
    }

    public List<RuleDetail> getRuleDetail() {
        return ruleDetail;
    }


    public void setStatus(RuleStatusType status) {
        this.status = status == null ? null : status.getId();
    }

    public RuleStatusType getStatus() {
        return status == null ? null : RuleStatusType.fromId(status);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setBaseRecordType(String baseRecordType) {
        this.baseRecordType = baseRecordType;
    }

    public String getBaseRecordType() {
        return baseRecordType;
    }

    public void setMatchingRecordType(String matchingRecordType) {
        this.matchingRecordType = matchingRecordType;
    }

    public String getMatchingRecordType() {
        return matchingRecordType;
    }

    public void setCaseSensitive(Boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    public Boolean getCaseSensitive() {
        return caseSensitive;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


}