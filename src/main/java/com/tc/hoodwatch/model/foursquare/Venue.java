package com.tc.hoodwatch.model.foursquare;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class Venue extends AbstractJson {
	@XmlElement
	public String id;
	@XmlElement
	public String name;
	@XmlElement
	public Object contact;
	@XmlElement
	public Location location;
	@XmlElement
	public List<Category> categories;
	@XmlElement
	public Boolean verified;
	@XmlElement
	public Stats stats;
	@XmlElement
	public String url;
	@XmlElement
	public Boolean allowMenuUrlEdit;
	@XmlElement
	public BeenHere beenHere;
	@XmlElement
	public Specials specials;
	@XmlElement
	public String referralId;
	@XmlElement
	public List venueChains;
	@XmlElement
	public Boolean hasPerk;
}
