package com.non.dta.service;

import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.cuba.core.Persistence;
import com.haulmont.cuba.core.PersistenceTools;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.entity.StandardEntity;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.core.sys.listener.EntityListenerManager;
import com.haulmont.cuba.security.entity.EntityOp;
import com.non.dta.entity.Rule;
import com.non.dta.entity.RuleDetail;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(DuplicateRuleService.NAME)
public class DuplicateRuleServiceBean implements DuplicateRuleService {
    @Inject
    private EntityListenerManager entityListenerManager;
    @Inject
    private DataManager dataManager;

    @Inject
    private Metadata metadata;
    @Inject
    private PersistenceTools persistenceTools;
    @Inject
    private Persistence persistence;
    @Inject
    private Logger log;

    @Override
    public List<MetaClass> getAccessibleEntities() {
        List<MetaClass> classList = new ArrayList<>();
        for (MetaClass metaClass : metadata.getTools().getAllPersistentMetaClasses()) {
            if (readPermitted(metaClass)) {
                Class javaClass = metaClass.getJavaClass();
                if (Entity.class.isAssignableFrom(javaClass)) {
                    classList.add(metaClass);
                }
            }
        }
        return classList;
    }

    private boolean readPermitted(MetaClass metaClass) {
        return entityOpPermitted(metaClass, EntityOp.READ);
    }

    private boolean entityOpPermitted(MetaClass metaClass, EntityOp entityOp) {
        Security security = AppBeans.get(Security.NAME);
        return security.isEntityOpPermitted(metaClass, entityOp);
    }


    @Override
    public boolean checkIfDuplicate(Entity entity) {
        boolean isDuplicate = false;
        List<Rule> rules = getConfiguredRulesForEntity(entity.getMetaClass().toString());

        for (Rule rule : rules) {
            if (rule.getRuleDetail() != null && !rule.getRuleDetail().isEmpty() && findDuplicateEntityByQuery(entity, rule) > 0) {
                isDuplicate = true;
                break;
            }
        }
        return isDuplicate;
    }


    private List<Rule> getConfiguredRulesForEntity(String entityType) {
        LoadContext<Rule> loadContext = LoadContext.create(Rule.class);
        LoadContext.Query query = LoadContext.createQuery("select e from nondta$Rule e where e.baseRecordType = :baseType and e.status = 20")
                .setParameter("baseType", entityType);
        loadContext.setView("rule-view");
        loadContext.setQuery(query);
        return dataManager.loadList(loadContext);
    }


    private boolean isRuleConfiguredForModifiedFields(Entity entity, Rule rule) {
        boolean isConfigured = false;
        for (RuleDetail detail : rule.getRuleDetail()) {
            if (persistenceTools.isDirty(entity, detail.getBaseRecordField())) {
                isConfigured = true;
                break;
            }
        }
        return isConfigured;
    }


    private int findDuplicateEntityByQuery(Entity entity, Rule rule) {
        StringBuilder sb = new StringBuilder();
        boolean isStandardEntity = false;
        sb.append("select e from " + rule.getMatchingRecordType() + " e where e.id <> :entityId ");
        Map<String, Object> params = new HashMap<>();
        for (RuleDetail detail : rule.getRuleDetail()) {
            Class matchingClass = (ReflectionUtils.findField(metadata.getClass(rule.getMatchingRecordType()).getJavaClass(), detail.getMatchingRecordField())).getType();
            if (matchingClass.getSuperclass().equals(StandardEntity.class)) {
                isStandardEntity = true;
            }
            sb.append(" and e.");
            sb.append(detail.getMatchingRecordField());
            if (isStandardEntity) {
                sb.append(".id");
            }
            sb.append(" = :");
            sb.append(detail.getMatchingRecordField());
            if (isStandardEntity) {
                params.put(detail.getMatchingRecordField(), entity.getValueEx(detail.getMatchingRecordField() + ".id"));
            } else {
                params.put(detail.getMatchingRecordField(), entity.getValue(detail.getMatchingRecordField()));
            }

            isStandardEntity = false;
        }

        LoadContext loadContext = LoadContext.create(metadata.getSession().getClass(rule.getMatchingRecordType()).getJavaClass());
        LoadContext.Query query = new LoadContext.Query(sb.toString());
        query.setParameter("entityId", entity.getId());
        log.debug(query.getQueryString());
        for (Map.Entry<String, Object> param : params.entrySet()) {
            query.setParameter(param.getKey(), param.getValue());
        }
        loadContext.setQuery(query);
        return (dataManager.loadList(loadContext)).size();
    }
}