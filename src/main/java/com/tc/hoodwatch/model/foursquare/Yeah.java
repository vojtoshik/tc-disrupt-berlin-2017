package com.tc.hoodwatch.model.foursquare;


// Yeah!

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Yeah extends AbstractJson {
	@XmlElement
	public Meta meta;
	@XmlElement
	public Response response;
}
