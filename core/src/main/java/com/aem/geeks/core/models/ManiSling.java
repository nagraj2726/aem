package com.aem.geeks.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(
    adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL
)
public class ManiSling {

    @ValueMapValue
    private String text;

    @ValueMapValue
    private String description;

    public String getText() {
        return text;
    }

    public String getDescription() {
        return description;
    }
}
