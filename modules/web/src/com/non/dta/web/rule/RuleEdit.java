package com.non.dta.web.rule;

import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.cuba.core.global.DataManager;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.actions.CreateAction;
import com.non.dta.entity.Rule;
import com.non.dta.service.DuplicateRuleService;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class RuleEdit extends AbstractEditor<Rule> {
    @Inject
    private FieldGroup fieldGroup;
    @Inject
    private Metadata metadata;
    @Inject
    private LookupField baseEntityLookup;
    @Inject
    private LookupField targetLookup;
    @Inject
    private DuplicateRuleService duplicateRuleService;
    @Inject
    private DataManager dataManager;
    @Named("fieldGroup.caseSensitive")
    private CheckBox caseSensitiveField;
    @Named("ruleDetailTable.create")
    private CreateAction ruleDetailTableCreateAction;


    @Override
    public void init(Map<String, Object> params) {
        baseEntityLookup.setOptionsMap(getEntitiesLookupFieldOptions());
        targetLookup.setOptionsMap(getEntitiesLookupFieldOptions());
        baseEntityLookup.setRequired(true);
        targetLookup.setRequired(true);
        if( caseSensitiveField.getValue() == null )
        {
            caseSensitiveField.setValue(false);
        }

        baseEntityLookup.addValueChangeListener(e -> {
            Rule rule = getItem();
            MetaClass baseClass = baseEntityLookup.getValue();
            rule.setBaseRecordType(baseClass.getName());
        });

        targetLookup.addValueChangeListener(e -> {
                    Rule rule = getItem();
                    MetaClass targetClass = targetLookup.getValue();
                    rule.setMatchingRecordType(targetClass.getName());
                }
        );

        super.postInit();
    }

    @Override
    protected void postInit() {
        if (getItem().getBaseRecordType() != null) {
            baseEntityLookup.setValue(metadata.getClass(getItem().getBaseRecordType()));
        }
        if (getItem().getMatchingRecordType() != null) {
            targetLookup.setValue(metadata.getClass(getItem().getMatchingRecordType()));
        }

        super.postInit();
    }

    protected Map<String, Object> getEntitiesLookupFieldOptions() {
        Map<String, Object> options = new TreeMap<>();
        for (MetaClass metaClass : duplicateRuleService.getAccessibleEntities()) {
            options.put(messages.getTools().getEntityCaption(metaClass) + " (" + metaClass.getName() + ")", metaClass);
        }
        return options;
    }

    @Override
    protected void postValidate(ValidationErrors errors) {
        if (duplicateRuleService.checkIfDuplicate(getItem())) {
            errors.add("Duplicate Records may not be created");
        } else {
            super.postValidate(errors);
        }
    }
}