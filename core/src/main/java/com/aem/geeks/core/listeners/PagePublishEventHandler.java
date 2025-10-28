package com.aem.geeks.core.listeners;

import com.day.cq.replication.ReplicationAction;
import com.day.cq.replication.ReplicationActionType;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import java.util.HashMap;
import java.util.Map;

@Component(service = EventHandler.class, immediate = true, property = {
        "event.topics=" + ReplicationAction.EVENT_TOPIC
})
public class PagePublishEventHandler implements EventHandler {

    private static final Logger LOG = LoggerFactory.getLogger(PagePublishEventHandler.class);

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Override
    public void handleEvent(Event event) {
        ReplicationAction action = ReplicationAction.fromEvent(event);

        if (action != null && ReplicationActionType.ACTIVATE.equals(action.getType())) {
            String pagePath = action.getPath();
            LOG.info("Page Published: {}", pagePath);

            try (ResourceResolver resourceResolver = getServiceResourceResolver()) {
                // Get the resource for the page
                Resource resource = resourceResolver.getResource(pagePath);

                if (resource != null) {
                    // Get the jcr:content node of the page
                    Resource jcrContentResource = resource.getChild("jcr:content");

                    if (jcrContentResource != null) {
                        Node jcrContentNode = jcrContentResource.adaptTo(Node.class);

                        if (jcrContentNode != null) {
                            // Add the 'changed' property to the jcr:content node
                            jcrContentNode.setProperty("changed", true);
                            resourceResolver.commit();
                            LOG.info("Property 'changed=true' added to: {}/jcr:content", pagePath);
                        } else {
                            LOG.error("Unable to adapt jcr:content resource to JCR Node for path: {}", pagePath);
                        }
                    } else {
                        LOG.error("jcr:content node not found for page: {}", pagePath);
                    }
                } else {
                    LOG.error("Resource not found for path: {}", pagePath);
                }
            } catch (Exception e) {
                LOG.error("Error while handling page publish event for path: {}", pagePath, e);
            }
        }
    }

    private ResourceResolver getServiceResourceResolver() throws Exception {
        Map<String, Object> authInfo = new HashMap<>();
        authInfo.put(ResourceResolverFactory.SUBSERVICE, "aemgeeks");
        return resourceResolverFactory.getServiceResourceResolver(authInfo);
    }
}
