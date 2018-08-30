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
import org.eclipse.persistence.jpa.jpql.parser.DateTime;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    @Override
    public List<MetaClass> getAccessibleEntities() {
        List<MetaClass> classList = new ArrayList<MetaClass>();
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
            List<String> params = buildParams(entity, rule);
            if (rule.getRuleDetail() != null && rule.getRuleDetail().size() > 0 && findDuplicateEntity(rule.getMatchingRecordType(), params) > 0) {
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


    private List<String> buildParams(Entity entity, Rule rule) {
        ArrayList<String> list = new ArrayList<String>(10);
        int i = 0;
        // id check

        StringBuffer sb = new StringBuffer();
        sb.append(" e.id <> '" + entity.getId() + "' ");
        list.add(sb.toString());
        for (RuleDetail detail : rule.getRuleDetail()) {
            i++;
            sb = new StringBuffer();
            sb.append(" e." + detail.getMatchingRecordField());
            Class matchingFieldType = null;
            boolean isStandardEntity = false;
            Class matchingClass = metadata.getClass(rule.getMatchingRecordType()).getJavaClass();
            try {
                matchingFieldType = matchingClass.getDeclaredField(detail.getMatchingRecordField()).getType();
                if (matchingFieldType.getSuperclass().equals(StandardEntity.class)) {
                    isStandardEntity = true;
                }
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
            if (isStandardEntity) {
                sb.append(".id");
            }

            sb.append(" = ");
            if (matchingFieldType == String.class || matchingFieldType == Date.class) {
                sb.append("'");
            }



            if (isStandardEntity) {
                StandardEntity standardEntity = entity.getValue(detail.getBaseRecordField());
                sb.append("'" + standardEntity.getId() + "'");
            } else if( matchingFieldType == DateTime.class )
            {
                Date date = entity.getValue(detail.getBaseRecordField());

                Timestamp timestamp = new Timestamp(date.getTime());
                sb.append(new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(timestamp));
            }
            else {
                sb.append(entity.getValue(detail.getBaseRecordField()).toString());
            }
            if (matchingFieldType == String.class || matchingFieldType == Date.class) {
                sb.append("'");
            }
            list.add(sb.toString());
        }

        return list;
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

    private int findDuplicateEntity(String type, List<String> params) {
        StringBuffer sb = new StringBuffer();
        sb.append("select e from " + type + " e where ");
        for (int i = 0; i < params.size(); i++) {
            if (i > 0) {
                sb.append(" and ");
            }
           sb.append(params.get(i));
        }
        LoadContext loadContext = LoadContext.create(metadata.getSession().getClass(type).getJavaClass());
        LoadContext.Query query = LoadContext.createQuery(sb.toString());
        loadContext.setQuery(query);
        return (dataManager.loadList(loadContext)).size();
    }
}