package com.aem.geeks.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(
    adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class ImageModel {

    @ValueMapValue
    private String imagePath;

    @ValueMapValue
    private String altText;

    public String getImagePath() {
        return imagePath;
    }

    public String getAltText() {
        return altText;
    }
}
