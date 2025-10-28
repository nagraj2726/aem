package com.aem.geeks.core.workflows;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.ParticipantStepChooser;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.metadata.MetaDataMap;

import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component(service = ParticipantStepChooser.class, immediate = true, property = {
        "chooser.label=Page Modification Logger Process" })
public class DynamicParticipantStepChooser implements ParticipantStepChooser {

    private static final Logger LOG = LoggerFactory.getLogger(DynamicParticipantStepChooser.class);

    @Reference
    private ResourceResolverFactory resourceResolverFactory;

    @Override
    public String getParticipant(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap metaDataMap)
            throws WorkflowException {
        try (ResourceResolver resourceResolver = getServiceResourceResolver()) {
            String payloadPath = workItem.getWorkflowData().getPayload().toString();
            LOG.info("Payload Path: {}", payloadPath);

            // Retrieve the dynamic participants list
            List<String> participants = getDynamicParticipants(payloadPath, resourceResolver);

            // Default to the first participant in the list
            if (!participants.isEmpty()) {
                LOG.info("Default participant: {}", participants.get(0));
                return participants.get(0);
            } else {
                LOG.warn("No participants found. Using fallback participant.");
                return "default-participant"; // Fallback participant
            }
        } catch (Exception e) {
            LOG.error("Error determining participant: {}", e.getMessage(), e);
            throw new WorkflowException("Error determining participant", e);
        }
    }

    /**
     * Retrieves a list of participants dynamically based on the payload path or
     * other logic.
     */
    private List<String> getDynamicParticipants(String payloadPath, ResourceResolver resourceResolver) {
        List<String> participants = new ArrayList<>();

        try {
            // Example logic to fetch participants dynamically
            if (payloadPath.startsWith("/content/aemgeeks")) {
                participants.add("content-reviewer");
                participants.add("content-editor");
            } else if (payloadPath.startsWith("/content/my-site/fr")) {
                participants.add("french-reviewer");
                participants.add("french-editor");
            } else {
                participants.add("default-reviewer");
            }

            LOG.info("Dynamic participants determined: {}", participants);
        } catch (Exception e) {
            LOG.error("Error fetching dynamic participants: {}", e.getMessage(), e);
        }

        return participants;
    }

    /**
     * Gets a service resource resolver.
     */
    private ResourceResolver getServiceResourceResolver() throws Exception {
        return resourceResolverFactory.getServiceResourceResolver(
                Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, "aemgeeks"));
    }
}
