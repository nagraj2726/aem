package com.aem.geeks.core.models;

import java.util.List;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = Resource.class,defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class Sidebar1 {

	
	@ValueMapValue
	public String logopath;
	
	@ValueMapValue
	public String logomobileimage;
	
	@ValueMapValue
	public String logolink;
	
	@ValueMapValue
	public String checkbox;
	
	@ValueMapValue
	public String country;
	

	public String getLogopath() {
		return logopath;
	}

	public String getLogomobileimage() {
		return logomobileimage;
	}

	public String getLogolink() {
		return logolink;
	}

	public String getCheckbox() {
		return checkbox;
	}
	
	public String getCountry() {
		return country;
	}
	
	@ChildResource
	public List<Multifield2> day2;
	
	
	public List<Multifield2> getDay2() {
		return day2;
	}
	
	@ChildResource
	public List<Multifield1> day1;
	
	
	public List<Multifield1> getDay1() {
		return day1;
	}
}