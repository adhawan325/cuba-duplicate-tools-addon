package com.non.dta.web.ruledetail;

import com.haulmont.chile.core.model.MetaClass;
import com.haulmont.chile.core.model.MetaProperty;
import com.haulmont.cuba.core.entity.Entity;
import com.haulmont.cuba.core.global.Metadata;
import com.haulmont.cuba.gui.components.AbstractEditor;
import com.haulmont.cuba.gui.components.LookupField;
import com.haulmont.cuba.gui.data.Datasource;
import com.non.dta.entity.Rule;
import com.non.dta.entity.RuleDetail;

import javax.inject.Inject;
import java.util.Map;
import java.util.TreeMap;

public class RuleDetailEdit extends AbstractEditor<RuleDetail> {
    @Inject
    private Datasource<Rule> ruleDs;

    @Inject
    private LookupField targetField;

    @Inject
    private LookupField baseField;
    @Inject
    private Metadata metadata;
    private Map<String, Object> myParams;

    @Override
    public void init(Map<String, Object> params) {
        targetField.setRequired(true);
        baseField.setRequired(true);
        myParams = params;
        super.init(params);
    }

    protected Map<String, Object> getAttributes(MetaClass metaClass) {
        Map<String, Object> options = new TreeMap<>();

        for (MetaProperty metaProperty : metaClass.getProperties()) {
                   options.put(metaProperty.getName().toString(), metaProperty);
            }
        return options;
    }

    @Override
    protected void postInit() {
        Rule rule = (Rule) myParams.get("rule");

        if( rule == null ) {
            System.out.println("No Rule PasseD");
            rule = ruleDs.getItem();
        }
        if( rule != null ) {
            baseField.setOptionsMap(getAttributes(metadata.getClass(rule.getBaseRecordType())));
            targetField.setOptionsMap(getAttributes(metadata.getClass(rule.getMatchingRecordType())));

            if (getItem().getBaseRecordField() != null) {
                baseField.setValue(metadata.getClass(rule.getBaseRecordType()).getProperty(getItem().getBaseRecordField()));
            }
            if (getItem().getMatchingRecordField() != null) {
                targetField.setValue(metadata.getClass(rule.getBaseRecordType()).getProperty(getItem().getMatchingRecordField()));
            }
        }
        super.postInit();
    }

    @Override
    protected boolean preCommit() {
        RuleDetail ruleDetail = getItem();
        MetaProperty baseAttribute = baseField.getValue();
        MetaProperty targetAttribute = targetField.getValue();
        if(baseAttribute.getJavaType() != targetAttribute.getJavaType())
        {
            showNotification("Attribute Types Do Not Match - base: " + baseAttribute.getJavaType().getSimpleName() + " match: " + targetAttribute.getJavaType().getSimpleName());
            return false;
        }
        ruleDetail.setBaseRecordField(baseAttribute.getName());
        ruleDetail.setMatchingRecordField(targetAttribute.getName());
        return super.preCommit();
    }

}