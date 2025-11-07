package com.aem.geeks.core.servlets;

import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.day.cq.dam.api.Asset;
import com.day.cq.dam.api.Rendition;
import com.day.cq.dam.commons.util.DamUtil;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.iterators.TransformIterator;
import org.apache.jackrabbit.JcrConstants;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.*;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.json.JSONArray;
import org.json.JSONException;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * A servlet that dynamically creates a Granite DataSource for dropdowns.
 */
@Component(
        service = Servlet.class,
        property = {
                Constants.SERVICE_DESCRIPTION + "=Dynamic DataSource Servlet",
                "sling.servlet.resourceTypes=" + DynamicDataSourceServlet.RESOURCE_TYPE
        }
)
public class DynamicDataSourceServlet extends SlingSafeMethodsServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicDataSourceServlet.class);

    /** The path under /apps where this servlet is registered **/
    protected static final String RESOURCE_TYPE = "/apps/aemgeeks/components/preferences";

    // Constants for dropdown types
    private static final String DATASOURCE = "datasource";
    private static final String DROPDOWN_SELECTOR = "dropdownSelector";

    private static final String COUNTRY_LIST = "countryList";
    private static final String COLOR_LIST = "colorList";
    private static final String FONT_LIST = "fontList";

    private static final String COUNTRY_LIST_PATH = "/content/dam/aemtutorials/country.json";
    private static final String COLOR_LIST_PATH = "/content/dam/aemtutorials/color.json";
    private static final String FONT_LIST_PATH = "/content/dam/aemtutorials/font.json";

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {

        try {
            ResourceResolver resolver = request.getResourceResolver();
            Resource currentResource = request.getResource();

            // Fetch datasource configuration from dialog
            String dropdownSelector = Optional.ofNullable(currentResource.getChild(DATASOURCE))
                    .map(r -> r.getValueMap().get(DROPDOWN_SELECTOR, String.class))
                    .orElse(null);

            if (dropdownSelector == null) {
                LOGGER.warn("Dropdown selector not configured in datasource node.");
                return;
            }

            // Get JSON file resource from DAM
            Resource jsonResource = getJsonResource(resolver, dropdownSelector);
            if (jsonResource == null) {
                LOGGER.error("Could not find JSON resource for selector: {}", dropdownSelector);
                return;
            }

            Asset asset = DamUtil.resolveToAsset(jsonResource);
            if (asset == null) {
                LOGGER.error("Failed to resolve DAM asset for resource: {}", jsonResource.getPath());
                return;
            }

            Rendition original = asset.getOriginal();
            InputStream inputStream = original.adaptTo(InputStream.class);
            if (inputStream == null) {
                LOGGER.error("Could not read input stream from asset: {}", asset.getPath());
                return;
            }

            // Read JSON content
            StringBuilder jsonContent = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    jsonContent.append(line);
                }
            }

            JSONArray jsonArray = new JSONArray(jsonContent.toString());
            Map<String, String> dataMap = new TreeMap<>();

            for (int i = 0; i < jsonArray.length(); i++) {
                dataMap.put(jsonArray.getJSONObject(i).getString("text"),
                        jsonArray.getJSONObject(i).getString("value"));
            }

            // Build SimpleDataSource
            DataSource dataSource = new SimpleDataSource(
                    new TransformIterator<>(dataMap.keySet().iterator(), (Transformer<String, Resource>) key -> {
                        ValueMap vm = new ValueMapDecorator(new HashMap<>());
                        vm.put("text", key);
                        vm.put("value", dataMap.get(key));
                        return new ValueMapResource(resolver, new ResourceMetadata(), JcrConstants.NT_UNSTRUCTURED, vm);
                    })
            );

            request.setAttribute(DataSource.class.getName(), dataSource);

        } catch (IOException | JSONException e) {
            LOGGER.error("Exception in DynamicDataSourceServlet: ", e);
        }
    }

    private Resource getJsonResource(ResourceResolver resolver, String dropdownSelector) {
        switch (dropdownSelector) {
            case COUNTRY_LIST:
                return resolver.getResource(COUNTRY_LIST_PATH);
            case COLOR_LIST:
                return resolver.getResource(COLOR_LIST_PATH);
            case FONT_LIST:
                return resolver.getResource(FONT_LIST_PATH);
            default:
                LOGGER.warn("Invalid dropdown selector: {}", dropdownSelector);
                return null;
        }
    }
}
