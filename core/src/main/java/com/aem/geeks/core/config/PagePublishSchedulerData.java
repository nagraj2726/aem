package com.aem.geeks.core.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "PagePublishSchedulerData", description = "PagePublishSchedulerData class file")
public @interface PagePublishSchedulerData {

    @AttributeDefinition(name = "Cron Expression For the Method", description = "Cron expression to run the method for every 3 seconds", defaultValue = "0/3 * * * * ?")
    public String cronExpression();

    @AttributeDefinition(name = "Parent for the Child Resources", description = "Parent path", defaultValue = "/content/Demo/us/en")
    public String pagePath();

    @AttributeDefinition(name = "Enable feature", description = "Enabling the Configuration class")
    public boolean enable();
}