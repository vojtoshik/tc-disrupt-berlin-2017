package com.tc.hoodwatch.model.foursquare;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Category extends AbstractJson {
	@XmlElement
	public String id;
	@XmlElement
	public String name;
	@XmlElement
	public String pluralName;
	@XmlElement
	public String shortName;
	@XmlElement
	public Icon icon;
	@XmlElement
	public Boolean primary;
}
