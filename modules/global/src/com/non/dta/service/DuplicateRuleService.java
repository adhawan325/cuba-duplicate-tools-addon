package com.non.dta.service;


import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.cuba.core.entity.Entity;

import java.util.List;

public interface DuplicateRuleService {
    String NAME = "dta_DuplicateRuleRegistrationService";


    List<MetaClass> getAccessibleEntities();

    boolean checkIfDuplicate(Entity entity);
}