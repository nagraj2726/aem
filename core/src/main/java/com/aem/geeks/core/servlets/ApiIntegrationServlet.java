package com.aem.geeks.core.servlets;

import java.io.IOException;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.*;
import org.apache.commons.io.IOUtils;
import org.apache.sling.api.*;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.jcr.api.SlingRepository;
import org.osgi.service.component.annotations.*;
import com.day.cq.commons.jcr.JcrUtil;

@Component(
    service = { Servlet.class },
    property = {
        "sling.servlet.paths=/bin/apiIntegration",
        "sling.servlet.methods=GET,POST"
    }
)
public class ApiIntegrationServlet extends SlingAllMethodsServlet {

    @Reference
    private SlingRepository repository;

    /**
     * Handles GET request - Fetches JSON data from an external API and stores it in CRXDE
     */
    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {

        // ✅ Reliable, open API (Google/ChatGPT alternative)
        String apiUrl = "https://jsonplaceholder.typicode.com/posts";

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet getRequest = new HttpGet(apiUrl);
            CloseableHttpResponse apiResponse = client.execute(getRequest);

            String jsonResponse = IOUtils.toString(apiResponse.getEntity().getContent(), "UTF-8");

            // ✅ Store response safely in CRXDE under /content/apiresponse
            storeResponseInCRX(jsonResponse);

            response.setContentType("text/plain");
            response.getWriter().write("✅ GET Response stored successfully in CRXDE.");
        } catch (Exception e) {
            response.setStatus(500);
            response.getWriter().write("❌ Error in GET: " + e.getMessage());
        }
    }

    /**
     * Handles POST request - Sends data to API and stores the response
     */
    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
            throws ServletException, IOException {

        // ✅ Simple test POST API
        String apiUrl = "https://postman-echo.com/post";
        String requestBody = "{\"name\":\"Nagaraj\",\"role\":\"AEM Developer\"}";

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost postRequest = new HttpPost(apiUrl);
            postRequest.setHeader("Content-Type", "application/json");
            postRequest.setEntity(new StringEntity(requestBody));

            CloseableHttpResponse apiResponse = client.execute(postRequest);
            String jsonResponse = IOUtils.toString(apiResponse.getEntity().getContent(), "UTF-8");

            // ✅ Store the response in CRXDE
            storeResponseInCRX(jsonResponse);

            response.setContentType("text/plain");
            response.getWriter().write("✅ POST Response stored successfully in CRXDE.");
        } catch (Exception e) {
            response.setStatus(500);
            response.getWriter().write("❌ Error in POST: " + e.getMessage());
        }
    }

    /**
     * Store API response data safely in CRXDE under /content/apiresponse
     */
    private void storeResponseInCRX(String jsonResponse) throws RepositoryException {
        // ✅ Use system-level JCR session (not tied to /bin path)
        Session session = null;
        try {
            session = repository.loginService(null, null); // Safe service login (no /bin persistence)

            // ✅ Create or get /content/apiresponse path
            Node dataNode = JcrUtil.createPath("/content/apiresponse", "nt:unstructured", session);

            // ✅ Create unique node for every API response
            Node newNode = dataNode.addNode("response-" + System.currentTimeMillis(), "nt:unstructured");
            newNode.setProperty("data", jsonResponse);

            // ✅ Save to JCR
            session.save();
        } finally {
            if (session != null && session.isLive()) {
                session.logout();
            }
        }
    }
}
