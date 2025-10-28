// package com.aem.geeks.core.services.impl;

// import com.adobe.granite.workflow.launcher.WorkflowLauncher;
// import com.adobe.granite.workflow.model.WorkflowModel;
// import org.apache.sling.api.resource.ResourceResolver;
// import org.apache.sling.api.resource.ResourceResolverFactory;
// import org.osgi.service.component.annotations.*;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

// import java.util.HashMap;
// import java.util.Map;

// @Component(service = Runnable.class, immediate = true)
// public class WorkflowLauncherConfigService implements Runnable {

//     private static final Logger LOG = LoggerFactory.getLogger(WorkflowLauncherConfigService.class);

//     @Reference
//     private WorkflowLauncher workflowLauncher;

//     @Reference
//     private ResourceResolverFactory resolverFactory;

//     @Activate
//     @Override
//     public void run() {
//         try (ResourceResolver resolver = getServiceResolver()) {
//             String workflowModel = "/var/workflow/models/page-creation";
//             String launcherPath = "/content/aemgeeks";

//             // Create launcher for Page Created
//             workflowLauncher.createLauncher(
//                     workflowModel,
//                     "Page Creation Launcher",
//                     launcherPath,
//                     "cq:Page",
//                     "nodeCreated",
//                     null,
//                     true,
//                     null
//             );

//             // Create launcher for Page Modified
//             workflowLauncher.createLauncher(
//                     workflowModel,
//                     "Page Modified Launcher",
//                     launcherPath,
//                     "cq:Page",
//                     "nodeModified",
//                     null,
//                     true,
//                     null
//             );

//             LOG.info(" Workflow Launchers created successfully for {}", launcherPath);
//         } catch (Exception e) {
//             LOG.error(" Failed to create workflow launchers", e);
//         }
//     }

//     private ResourceResolver getServiceResolver() throws Exception {
//         Map<String, Object> params = new HashMap<>();
//         params.put(ResourceResolverFactory.SUBSERVICE, "aemgeeks");
//         return resolverFactory.getServiceResourceResolver(params);
//     }
// }
