package com.aem.geeks.core.models;


import com.day.cq.replication.Replicator;
import com.day.cq.replication.ReplicationActionType;
import com.day.cq.replication.ReplicationException;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Session;
import java.util.Collections;

@Component(service = JobConsumer.class, immediate = true, property = {
        JobConsumer.PROPERTY_TOPICS + "=com/example/assetPublishJob"
})
public class AssetPublishJobConsumer implements JobConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(AssetPublishJobConsumer.class);
    private static final String SERVICE_USER = "aemgeeks"; // Ensure this user exists in AEM

    @Reference
    private Replicator replicator;

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Override
    public JobResult process(Job job) {
        String assetPath = job.getProperty("assetPath", String.class);
        if (assetPath == null || assetPath.isEmpty()) {
            LOG.error("Asset path is null or empty. Job cannot proceed.");
            return JobResult.FAILED;
        }

        try (ResourceResolver resolver = getServiceResourceResolver()) {
            if (resolver == null) {
                LOG.error("Service Resource Resolver is null. Aborting job.");
                return JobResult.FAILED;
            }

            Session session = resolver.adaptTo(Session.class);
            if (session == null) {
                LOG.error("Failed to adapt ResourceResolver to JCR Session.");
                return JobResult.FAILED;
            }

            LOG.info("Publishing asset: {}", assetPath);
            replicator.replicate(session, ReplicationActionType.ACTIVATE, assetPath);
            LOG.info("Successfully published asset: {}", assetPath);

            return JobResult.OK;
        } catch (ReplicationException e) {
            LOG.error("Replication failed for asset: {}", assetPath, e);
        } catch (Exception e) {
            LOG.error("Unexpected error in job processing", e);
        }

        return JobResult.FAILED;
    }

    private ResourceResolver getServiceResourceResolver() {
        try {
            return resourceResolverFactory.getServiceResourceResolver(
                    Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, SERVICE_USER));
        } catch (Exception e) {
            LOG.error("Failed to get Service Resource Resolver for user: {}", SERVICE_USER, e);
            return null;
        }
    }
}
