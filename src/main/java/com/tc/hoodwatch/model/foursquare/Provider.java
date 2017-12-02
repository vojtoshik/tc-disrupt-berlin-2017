package com.tc.hoodwatch.model.foursquare;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Provider extends AbstractJson {
	@XmlElement
	public String name;
	@XmlElement
	public Icon iconUrl;
	@XmlElement
	public String urlText;
}
