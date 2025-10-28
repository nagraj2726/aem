package com.aem.geeks.core.models;

import java.util.List;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;


@Model(adaptables = Resource.class,defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class CarouselParent {
@ValueMapValue
private String textfield;

@ValueMapValue
private String pathfield;
@ChildResource
public List<CarouselChild>bookdetailswithwriter;
public String getTextfield() {
    return textfield;
}
public String getPathfield() {
    return pathfield;
}
public List<CarouselChild> getBookdetailswithwriter() {
    return bookdetailswithwriter;
}

}