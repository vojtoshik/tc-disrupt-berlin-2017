package com.tc.hoodwatch.model.foursquare;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class Events extends AbstractJson {
	@XmlElement
	public Integer count;
	@XmlElement
	public String summary;
	@XmlElement
	public List<Event> items;
}
