package com.tc.hoodwatch.model.foursquare;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Stats extends AbstractJson {
	@XmlElement
	public Integer checkinsCount;
	@XmlElement
	public Integer usersCount;
	@XmlElement
	public Integer tipCount;
}
