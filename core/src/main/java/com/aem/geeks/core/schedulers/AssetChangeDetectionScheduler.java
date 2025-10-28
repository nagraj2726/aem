package com.aem.geeks.core.schedulers;


import org.apache.sling.event.jobs.JobManager;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.LoginException;
import org.osgi.service.component.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;

@Component(service = Runnable.class, immediate = true)
@Designate(ocd = AssetChangeDetectionScheduler.Config.class)
public class AssetChangeDetectionScheduler implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(AssetChangeDetectionScheduler.class);
    private static final String JOB_TOPIC = "com/example/assetPublishJob";

    @Reference
    private Scheduler scheduler;

    @Reference
    private JobManager jobManager;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    private String damPath;
    private int timeThreshold;

    @ObjectClassDefinition(name = "Asset Change Detection Scheduler")
    public @interface Config {
        @AttributeDefinition(name = "DAM Path", description = "Specify the DAM path to monitor")
        String dam_path() default "/content/dam/anime";

        @AttributeDefinition(name = "Time Threshold (Minutes)", description = "Threshold time to detect modifications")
        int time_threshold() default 5;

        @AttributeDefinition(name = "Scheduler Expression", description = "CRON expression for scheduling")
        String scheduler_expression() default "0 */5 * * * ?";
    }

    @Activate
    @Modified
    protected void activate(Config config) {
        this.damPath = config.dam_path();
        this.timeThreshold = config.time_threshold() * 60 * 1000; // Convert minutes to milliseconds

        ScheduleOptions options = scheduler.EXPR(config.scheduler_expression());
        options.canRunConcurrently(false);
        scheduler.schedule(this, options);
        LOG.info("Asset Change Detection Scheduler Activated for path: {}", damPath);
    }

    @Override
    public void run() {
        LOG.info("Executing Asset Change Detection Scheduler for {}", damPath);

        try (ResourceResolver resolver = getServiceResourceResolver()) {
            if (resolver != null) {
                Resource damResource = resolver.getResource(damPath);
                if (damResource != null) {
                    Iterator<Resource> assets = damResource.listChildren();
                    long currentTime = System.currentTimeMillis();

                    while (assets.hasNext()) {
                        Resource assetResource = assets.next();
                        Long lastModified = assetResource.getValueMap().get("jcr:content/jcr:lastModified", Long.class);

                        if (lastModified != null && (currentTime - lastModified) < timeThreshold) {
                            LOG.info("Detected modified asset: {}", assetResource.getPath());

                            // Create a Sling Job
                            Map<String, Object> jobProperties = new HashMap<>();
                            jobProperties.put("assetPath", assetResource.getPath());
                            Job job = jobManager.addJob(JOB_TOPIC, jobProperties);

                            if (job != null) {
                                LOG.info("Job Created for asset publishing: {}", assetResource.getPath());
                            } else {
                                LOG.error("Failed to create job for asset publishing: {}", assetResource.getPath());
                            }
                        }
                    }
                } else {
                    LOG.warn("DAM path does not exist: {}", damPath);
                }
            }
        } catch (Exception e) {
            LOG.error("Error in Asset Change Detection Scheduler", e);
        }
    }

    private ResourceResolver getServiceResourceResolver() {
        Map<String, Object> params = new HashMap<>();
        params.put(ResourceResolverFactory.SUBSERVICE, "aemgeeks");

        try {
            return resourceResolverFactory.getServiceResourceResolver(params);
        } catch (LoginException e) {
            LOG.error("Failed to get Service Resource Resolver", e);
        }
        return null;
    }
}
