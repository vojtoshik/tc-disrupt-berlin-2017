package com.tc.hoodwatch.model.foursquare;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class Event extends AbstractJson {
	@XmlElement
	public String id;
	@XmlElement
	public String name;
	@XmlElement
	public List<Category> categories;
	@XmlElement
	public Long startAt;
	@XmlElement
	public Long endAt;
	@XmlElement
	public Boolean allDay;
	@XmlElement
	public String timeZone;
	@XmlElement
	public String text;
	@XmlElement
	public String url;
	@XmlElement
	public List images;
	@XmlElement
	public Provider provider;
	@XmlElement
	public Stats stats;
}
