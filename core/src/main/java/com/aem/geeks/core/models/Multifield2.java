package com.aem.geeks.core.models;

import java.util.List;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;


@Model(adaptables = Resource.class,defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class Multifield2 
{
	@ValueMapValue
	public String desktopicon;
	
	@ValueMapValue
	public String mobileicon;
	
	

	public String getDesktopicon() {
		return desktopicon;
	}

	public String getMobileicon() {
		return mobileicon;
	}
	
	@ChildResource
	public List<Nested> nested;
	
	
	public List<Nested> getNested() {
		return nested;
	}
	
	
}