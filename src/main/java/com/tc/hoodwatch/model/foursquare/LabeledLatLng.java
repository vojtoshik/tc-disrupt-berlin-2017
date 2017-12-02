package com.tc.hoodwatch.model.foursquare;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class LabeledLatLng extends AbstractJson {
	@XmlElement
	public String label;
	@XmlElement
	public Integer lat;
	@XmlElement
	public Integer lng;
}
