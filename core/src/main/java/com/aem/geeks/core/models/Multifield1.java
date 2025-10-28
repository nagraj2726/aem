package com.aem.geeks.core.models;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
@Model(adaptables = Resource.class,defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class Multifield1 
{
	@ValueMapValue
	public String name;
	
	public String getName() {
		return name;
	}

	@ValueMapValue
	public String image;
	
	public String getImage() {
		return image;
	}
}