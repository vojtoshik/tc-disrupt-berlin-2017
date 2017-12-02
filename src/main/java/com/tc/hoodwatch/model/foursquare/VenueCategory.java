package com.tc.hoodwatch.model.foursquare;


public enum VenueCategory {
	ARTS_ENTERTAINMENT("Arts & Entertainment", "4d4b7104d754a06370d81259"),
	OUTDOORS_RECREATION("Outdoors & Recreation", "4d4b7105d754a06377d81259"),
	FOOD("Food", "4d4b7105d754a06374d81259"),
	INDUSTRIAL_ESTATE("Industrial Estate", "56aa371be4b08b9a8d5734d7"),
	PRISON("Prison", "5310b8e5bcbc57f1066bcbf1"),
	WASTE_FACILITY("Waste Facility", "58daa1558bbb0b01f18ec1ac");

	private String friendlyName;
	private String id;

	VenueCategory(String friendlyName, String id) {
		this.friendlyName = friendlyName;
		this.id = id;
	}

	public String getFriendlyName() {
		return friendlyName;
	}

	public String getId() {
		return id;
	}
}
