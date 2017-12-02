package com.tc.hoodwatch.model.foursquare;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Meta extends AbstractJson {
	@XmlElement
	public Integer code;
	@XmlElement
	public String requestId;
}
