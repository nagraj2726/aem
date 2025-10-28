package com.aem.geeks.core.schedulers;


import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = CustomScheduler.class, immediate = true)
@Designate(ocd = CustomScheduler.Config.class)
public class CustomScheduler implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(CustomScheduler.class);

    @Reference
    private Scheduler scheduler;

    private static final String JOB_NAME = "CustomSchedulerJob";

    private String clientId;
    private String apiToken;
    private String pagePath;

    // Configuration Interface
    @org.osgi.service.metatype.annotations.ObjectClassDefinition(name = "Custom Scheduler Configuration")
    public @interface Config {

        @AttributeDefinition(name = "Cron Expression", description = "Cron expression for scheduling the job")
        String cronExpression() default "0 * * * * ?"; // Every minute

        @AttributeDefinition(name = "Client ID", description = "Client ID for the service")
        String clientId() default "5678987@#S7";

        @AttributeDefinition(name = "API Token", description = "API Token for the service")
        String apiToken() default "67888";

        @AttributeDefinition(name = "Page Path", description = "Page path to validate")
        String pagePath() default "/content/aemgeeks/us/en/hulk";
    }

    private String cronExpression;

    @Activate
    protected void activate(Config config) {
        LOG.info("Activating Custom Scheduler");

        // Read OSGi Configuration
        this.cronExpression = config.cronExpression();
        this.clientId = config.clientId();
        this.apiToken = config.apiToken();
        this.pagePath = config.pagePath();

        LOG.info("Loaded configuration: clientId={}, apiToken={}, pagePath={}, cronExpression={}",
                clientId, apiToken, pagePath, cronExpression);

        // Schedule the job
        ScheduleOptions scheduleOptions = scheduler.EXPR(cronExpression);
        scheduleOptions.name(JOB_NAME);
        scheduleOptions.canRunConcurrently(false);

        scheduler.schedule(this, scheduleOptions);
        LOG.info("Custom Scheduler activated with cron expression: {}", cronExpression);
    }

    @Deactivate
    protected void deactivate() {
        LOG.info("Deactivating Custom Scheduler");
        scheduler.unschedule(JOB_NAME);
    }

    @Override
    public void run() {
        try {
            
            // Inject configuration into the shared service
            LOG.info("Scheduler executed: clientId={}, apiToken={}, pagePath={}",
                    clientId, apiToken, pagePath);
        } catch (Exception e) {
            LOG.error("Error in Custom Scheduler execution: ", e);
        }
    }

    public String getClientId() {
        return clientId;
    }

    public String getApiToken() {
        return apiToken;
    }

    public String getPagePath() {
        return pagePath;
    }
}
