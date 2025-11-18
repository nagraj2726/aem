package com.aem.geeks.core.config;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.osgi.service.metatype.annotations.AttributeType;

@ObjectClassDefinition(
    name ="Mani Kanta Scheduler Configuration",
    description = "Configuration for Manikanta Scheduler"
)
public @interface ManiKantaSch {
    @AttributeDefinition(
    name= "Scheduler Name",
    description = "Name of the Scheduler",
    type = AttributeType.STRING
    )
    public String SchedulerName() default "ManiKanta Scheduler";
  @AttributeDefinition(
    name= "Scheduler Expression",
    description = "Cron Expression for the Scheduler",
    type = AttributeType.STRING
  )
    public String cronExpression() default "0 0/2 * * * ?";

}
