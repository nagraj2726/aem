package com.aem.geeks.core.schedulers;

import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;

@ObjectClassDefinition(
    name = "Mani Scheduler Configuration",
    description = "Configuration for Mani Scheduler"
)
public @interface ManiSch {//
    @AttributeDefinition(
        name = "Scheduler Enabled",
        description = "Enable or disable the scheduler",
        type = AttributeType.STRING
    )
    public String schedulerName() default "false";

    @AttributeDefinition(
        name = "Scheduler Expression",
        description = "Cron expression for the scheduler",//
        type = AttributeType.STRING//
    )
public String cronExpression() default "0 0/2 * * * ?";//
}
