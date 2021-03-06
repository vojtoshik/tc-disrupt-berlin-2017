package com.tc.hoodwatch.model.foursquare;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class Icon extends AbstractJson {
	@XmlElement
	public String prefix;
	@XmlElement
	public String suffix;
	@XmlElement
	public String name;
	@XmlElement
	public List<Integer> sizes;
}
