package com.aem.geeks.core.listeners;

import java.util.HashMap;
import java.util.Map;

import javax.jcr.Session;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;

import org.apache.sling.jcr.api.SlingRepository;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.workflow.WorkflowException;
import com.day.cq.workflow.WorkflowService;
import com.day.cq.workflow.WorkflowSession;
import com.day.cq.workflow.exec.WorkflowData;
import com.day.cq.workflow.model.WorkflowModel;

@Component(immediate = true)
public class PageCreatedTask implements EventListener {

    private static final Logger log = LoggerFactory.getLogger(PageCreatedTask.class);

    @Reference
    private WorkflowService workflowService;

    @Reference
    private ResourceResolverFactory resolverFactory;

    @Reference
    private SlingRepository repository;

    private Session observationSession;

    @Activate
    protected void activate() {
        try {
            observationSession = repository.loginService("aemgeeks", null);

            observationSession.getWorkspace().getObservationManager().addEventListener(
                this,
                Event.NODE_ADDED,
                "/content", // path to observe
                true,       // isDeep
                null,       // no UUIDs
                null,       // no node types filter
                false       // no local
            );

            log.info("PageCreationEventListener activated");
        } catch (Exception e) {
            log.error("Error activating PageCreationEventListener", e);
        }
    }

    @Deactivate
    protected void deactivate() {
        if (observationSession != null) {
            try {
                observationSession.getWorkspace().getObservationManager().removeEventListener(this);
                observationSession.logout();
            } catch (Exception e) {
                log.error("Error deactivating PageCreationEventListener", e);
            }
        }
    }

    @Override
    public void onEvent(EventIterator events) {
        while (events.hasNext()) {
            Event event = events.nextEvent();
            try {
                String path = event.getPath();
                handleResourceAddedEvent(path);
            } catch (Exception e) {
                log.error("Error handling event", e);
            }
        }
    }

    private void handleResourceAddedEvent(String path) {
        ResourceResolver resourceResolver = null;
        try {
            Map<String, Object> param = new HashMap<>();
            param.put(ResourceResolverFactory.SUBSERVICE, "aemgeeks");

            resourceResolver = resolverFactory.getServiceResourceResolver(param);
            Resource resource = resourceResolver.getResource(path);

            if (resource != null && resource.isResourceType("cq:Page")) {
                log.info("Page created at path: {}", path);
                startWorkflow(path);
            }
        } catch (LoginException e) {
            log.error("Error obtaining resource resolver", e);
        } finally {
            if (resourceResolver != null) {
                resourceResolver.close();
            }
        }
    }

    private void startWorkflow(String payloadPath) {
        Map<String, Object> param = new HashMap<>();
        param.put(ResourceResolverFactory.SUBSERVICE, "aemgeeks");

        try (ResourceResolver resolver = resolverFactory.getServiceResourceResolver(param)) {
            Session session = resolver.adaptTo(Session.class);
            WorkflowSession workflowSession = workflowService.getWorkflowSession(session);

            String workflowModelPath = "/var/workflow/models/spidey";
            WorkflowModel workflowModel = workflowSession.getModel(workflowModelPath);
            WorkflowData workflowData = workflowSession.newWorkflowData("JCR_PATH", payloadPath);

            workflowSession.startWorkflow(workflowModel, workflowData);
            log.info("Workflow started for payload: {}", payloadPath);

        } catch (LoginException e) {
            log.error("LoginException while starting workflow", e);
        } catch (WorkflowException e) {
            log.error("WorkflowException while starting workflow", e);
        } catch (Exception e) {
            log.error("Exception while starting workflow", e);
        }
    }
}