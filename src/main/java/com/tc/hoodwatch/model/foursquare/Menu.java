package com.tc.hoodwatch.model.foursquare;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Menu extends AbstractJson {
	@XmlElement
	public String type;
	@XmlElement
	public String label;
	@XmlElement
	public String anchor;
	@XmlElement
	public String url;
	@XmlElement
	public String mobileUrl;
	@XmlElement
	public String externalUrl;
}
