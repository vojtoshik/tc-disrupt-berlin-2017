package com.tc.hoodwatch.model.foursquare;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Contact extends AbstractJson {
	@XmlElement
	public String phone;
	@XmlElement
	public String formattedPhone;
}
