package com.aem.geeks.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ImageModel {

    @ValueMapValue
    private String image;  // maps to ./image from dialog

    @ValueMapValue
    private String altText; // maps to ./altText from dialog

    public String getImage() {
        return image;
    }

    public String getAltText() {
        return altText;
    }
}
