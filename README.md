# cuba-duplicate-tools-addon

This add-on allows you to configure duplicate detection rules for entities.  You define a "base" and a "match" entity, define which fields to "match" on, and add a small snippet of code to your UI.  The add-on does the rest.

To get: add to your project from bintray here: https://bintray.com/adhawan325/cuba-duplicate-tools-addon/dta

This add-on allows cuba platform users to configure rules to prevent "duplicate" data entry into the system.  For example, if you want to ensure that no two customer's can have the same email address, you can set up a duplicate detection rule to prevent users from creating duplicate data.
This add-on is beneficial because it supports duplicate detection across entities (so customer and user records can be set up to not allow the same email, for example).
Additionally, Associations to other records are supported (as long as the other record extends StandardEntity).


To use in your UI:
1. Add the menu item in your web-menu.xml at a location of your choosing: 
```
<item screen="dta$Rule.browse"/>
```
2. Use this screen to setup rules.  Rules can be for same entity, or different entities.  
3. Once rules are setup, you can use the add-on in your UI.  Add the service to your controller and use the postValidateHook to invoke the duplicate check method
```
    @Inject
    private DuplicateRuleService duplicateRuleService;
```

```
    @Override
    protected void postValidate(ValidationErrors errors) {
        if (duplicateRuleService.checkIfDuplicate(getItem())) {
            errors.add("Duplicate Records may not be created");
        } else {
            super.postValidate(errors);
        }
    }
```

![screenshot](https://s3-us-west-1.amazonaws.com/909technologies/Capture.PNG)