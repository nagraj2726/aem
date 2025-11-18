package com.aem.geeks.core.workflows;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowData;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;

import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(
        service = WorkflowProcess.class,
        property = {
                "process.label=Geeks Custom Workflow Process"
        }
)
public class WorkflowSession implements WorkflowProcess {

    private static final Logger LOG = LoggerFactory.getLogger(WorkflowSession.class);

    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap args)
            throws WorkflowException {

        try {
            LOG.info("----- Custom Workflow Started -----");

            // Payload (path of resource/page/asset)
            WorkflowData workflowData = workItem.getWorkflowData();
            String payload = workflowData.getPayload().toString();

            LOG.info("Payload Path: {}", payload);

            // Getting process arguments
            String processArgs = args.get("PROCESS_ARGS", "");
            LOG.info("Process Arguments: {}", processArgs);

            // Add your logic here
            LOG.info("Your custom logic is executed here.");

            LOG.info("----- Custom Workflow Completed -----");

        } catch (Exception e) {
            LOG.error("Error in custom workflow: {}", e.getMessage(), e);
            throw new WorkflowException(e);
        }
    }

    @Override
    public void execute(WorkItem arg0, com.adobe.granite.workflow.WorkflowSession arg1, MetaDataMap arg2)
            throws WorkflowException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'execute'");
    }
}