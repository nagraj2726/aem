package com.aem.geeks.core.schedulers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.jcr.Session;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.commons.scheduler.ScheduleOptions;
import org.apache.sling.commons.scheduler.Scheduler;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aem.geeks.core.config.PagePublishSchedulerData;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import com.day.cq.replication.Replicator;

@Component(service = Runnable.class, immediate = true)
@Designate(ocd = PagePublishSchedulerData.class)

public class PagePublishScheduler implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(PagePublishScheduler.class);

    @Reference
    private Scheduler scheduler;

    @Reference
    private Replicator replicator;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    private String cronExpression;

    private String pagePath;

    @Activate
    protected void activate(PagePublishSchedulerData pagePublishSchedulerData) {
        this.cronExpression = pagePublishSchedulerData.cronExpression();
        this.pagePath = pagePublishSchedulerData.pagePath();

        if (pagePublishSchedulerData.enable()) {
            ScheduleOptions options = scheduler.EXPR(cronExpression);
            options.name("PagePublishScheduler");
            options.canRunConcurrently(false);
            scheduler.schedule(this, options);
        }

        LOG.info("Scheduler is activated for the cron expression: {}", cronExpression);
    }

    @Deactivate
    protected void deactivate(PagePublishSchedulerData pagePublishSchedulerData) {
        scheduler.unschedule(cronExpression);
    }

    @Override
    public void run() {
        LOG.info("Scheduler triggered now Attempting to publish page: {}", pagePath);
        try (ResourceResolver resourceResolver = getAdminResourceResolver()) {
            Session session = resourceResolver.adaptTo(Session.class);
            if (session == null) {
                LOG.error("Session could not be obtained. Replication aborted.");
                return;
            }
            Resource rootResource = resourceResolver.getResource(pagePath);
            if (rootResource == null) {
                LOG.error("Page path {} does not exist. Aborting replication.", pagePath);
                return;
            }
            publishChildPages(rootResource, session);
            LOG.info("All child pages under {} have been successfully published.", pagePath);
        } catch (Exception e) {
            LOG.error("Error occurred while publishing the page: {}", pagePath, e);
        }
    }

    private void publishChildPages(Resource resource, Session session) {
        try {
            // Publish the current resource (page)
            if (isPage(resource)) {
                LOG.info("Publishing page: {}", resource.getPath());
                replicator.replicate(session, ReplicationActionType.ACTIVATE, resource.getPath());
            }

            // Using Iterator to iterate over children and publish them (only pages)
            Iterator<Resource> childPagePaths = resource.listChildren();
            while (childPagePaths.hasNext()) {
                Resource child = childPagePaths.next();
                if (isPage(child)) {
                    publishChildPages(child, session); // Recursively publish child pages
                }
            }
        } catch (ReplicationException e) {
            LOG.error("Error publishing page: {}", resource.getPath(), e);
        }
    }

    // Helper method to check if a resource is a page
    private boolean isPage(Resource resource) {
        return resource != null && "cq:Page".equals(resource.getResourceType());
    }

    private ResourceResolver getAdminResourceResolver() throws Exception {
        Map<String, Object> authInfo = new HashMap<>();
        authInfo.put(ResourceResolverFactory.SUBSERVICE, "schedulerService");
        return resourceResolverFactory.getServiceResourceResolver(authInfo);
    }
}