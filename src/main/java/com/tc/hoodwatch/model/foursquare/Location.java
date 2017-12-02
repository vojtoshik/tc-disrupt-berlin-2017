package com.tc.hoodwatch.model.foursquare;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class Location extends AbstractJson {
	@XmlElement
	public String address;
	@XmlElement
	public String crossStreet;
	@XmlElement
	public Double lat;
	@XmlElement
	public Double lng;
	@XmlElement
	public List<LabeledLatLng> labeledLatLngs;
	@XmlElement
	public Double distance;
	@XmlElement
	public String postalCode;
	@XmlElement
	public String cc;
	@XmlElement
	public String neighborhood;
	@XmlElement
	public String city;
	@XmlElement
	public String state;
	@XmlElement
	public String country;
	@XmlElement
	public List<String> formattedAddress;
}
