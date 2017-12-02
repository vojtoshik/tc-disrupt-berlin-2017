package com.tc.hoodwatch.model.foursquare;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class BeenHere extends AbstractJson {
	@XmlElement
	public Integer lastCheckinExpiredAt;
}
