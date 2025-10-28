package com.aem.geeks.core.schedulers;


import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.Replicator;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Session;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@Component(service = Runnable.class, immediate = true)
@Designate(ocd = PageScheduler.Config.class)
public class PageScheduler implements Runnable {

    @ObjectClassDefinition(name = "Page Publish/Unpublish Scheduler")
    public @interface Config {
        @AttributeDefinition(name = "Cron Expression", description = "Cron expression for scheduling")
        String scheduler_expression() default "0 0/3 * * * ?";
    }

    private static final Logger LOG = LoggerFactory.getLogger(PageScheduler.class);

    @Reference
    private Scheduler scheduler;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Reference
    private Replicator replicator;

    private ScheduleOptions scheduleOptions;

    @Activate
    protected void activate(Config config) {
        scheduleOptions = scheduler.EXPR(config.scheduler_expression());
        scheduleOptions.name("page-scheduler");
        scheduler.schedule(this, scheduleOptions);
        LOG.info("Page Scheduler Activated with Cron Expression: {}", config.scheduler_expression());
    }

    @Deactivate
    protected void deactivate() {
        scheduler.unschedule("page-scheduler");
        LOG.info("Page Scheduler Deactivated");
    }

    @Override
    public void run() {
        try (ResourceResolver resourceResolver = getServiceResourceResolver()) {
            String basePath = "/content/aemgeeks"; // Base path for pages
            Resource baseResource = resourceResolver.getResource(basePath);

            if (baseResource != null) {
                Calendar now = Calendar.getInstance();
                LOG.info("Scheduler triggered. Current time: {}", now.getTime());

                for (Resource page : baseResource.getChildren()) {
                    processPage(page, now, resourceResolver);
                }
            } else {
                LOG.warn("No base resource found at path: {}", basePath);
            }
        } catch (Exception e) {
            LOG.error("Error during page scheduling task", e);
        }
    }

    private void processPage(Resource page, Calendar now, ResourceResolver resourceResolver) {
        Resource contentNode = page.getChild("jcr:content");

        if (contentNode != null) {
            ValueMap properties = contentNode.getValueMap();

            // Log all properties of the jcr:content node for debugging

            // Check if the expiry property exists and is of type String
            if (properties.containsKey("expiry")) {
                String expiryString = properties.get("expiry", String.class);
                LOG.info("Expiry Date : {}", expiryString);

                if (expiryString != null) {
                    Calendar expiryDate = parseExpiryDate(expiryString);
                    if (expiryDate != null) {
                        if (expiryDate.before(now)) {
                            unpublishPage(page.getPath(), resourceResolver);
                        } else {
                            publishPage(page.getPath(), resourceResolver);
                        }
                    } else {
                        LOG.warn("Invalid expiry date format for page: {}", page.getPath());
                    }
                } else {
                    LOG.warn("Expiry date is null for page: {}", page.getPath());
                }
            } else {
                LOG.warn("Expiry property not found for page: {}", page.getPath());
            }
        } else {
            LOG.warn("No jcr:content node found for page: {}", page.getPath());
        }
    }

    private Calendar parseExpiryDate(String expiryString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateFormat.parse(expiryString));
            return calendar;
        } catch (ParseException e) {
            LOG.error("Failed to parse expiry date: {}", expiryString, e);
            return null;
        }
    }

    private void publishPage(String path, ResourceResolver resourceResolver) {
        try {
            replicator.replicate(resourceResolver.adaptTo(Session.class), ReplicationActionType.ACTIVATE, path);
            LOG.info("Page Published: {}", path);
        } catch (Exception e) {
            LOG.error("Failed to publish page: {}", path, e);
        }
    }

    private void unpublishPage(String path, ResourceResolver resourceResolver) {
        try {
            replicator.replicate(resourceResolver.adaptTo(Session.class), ReplicationActionType.DEACTIVATE, path);
            LOG.info("Page Unpublished: {}", path);
        } catch (Exception e) {
            LOG.error("Failed to unpublish page: {}", path, e);
        }
    }

    private ResourceResolver getServiceResourceResolver() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put(ResourceResolverFactory.SUBSERVICE, "aemgeeks");
        return resourceResolverFactory.getServiceResourceResolver(params);
    }
}