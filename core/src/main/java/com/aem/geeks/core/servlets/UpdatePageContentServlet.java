package com.aem.geeks.core.servlets;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.aem.geeks.core.schedulers.CustomScheduler;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.io.IOException;

@Component(service = javax.servlet.Servlet.class, property = {
        "sling.servlet.paths=/bin/updatePageContent",
        "sling.servlet.methods=GET"
})
public class UpdatePageContentServlet extends SlingAllMethodsServlet {

    @Reference
    private CustomScheduler customSchedulerService;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        String pagePath = customSchedulerService.getPagePath();
        String clientId = customSchedulerService.getClientId();
        String apiToken = customSchedulerService.getApiToken();

        Session session = request.getResourceResolver().adaptTo(Session.class);
        if (session == null) {
            response.getWriter().write("Unable to adapt to JCR session.");
            return;
        }

        try {
            // Check if the page path exists
            if (!session.nodeExists(pagePath)) {
                response.getWriter().write("Invalid page path: " + pagePath);
                return;
            }

            // Get the jcr:content node of the page
            Node pageNode = session.getNode(pagePath);
            if (!pageNode.hasNode("jcr:content")) {
                response.getWriter().write("The path does not have a jcr:content node.");
                return;
            }

            Node contentNode = pageNode.getNode("jcr:content");

            // Add clientId and apiToken as properties
            contentNode.setProperty("clientId", clientId);
            contentNode.setProperty("apiToken", apiToken);

            // Save changes
            session.save();

            response.getWriter().write("Updated jcr:content node with clientId and apiToken.");
        } catch (RepositoryException e) {
            response.getWriter().write("Error while accessing JCR: " + e.getMessage());
        }
    }
}

