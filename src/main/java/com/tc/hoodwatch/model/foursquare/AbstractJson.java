package com.tc.hoodwatch.model.foursquare;

import com.tc.hoodwatch.util.JsonUtil;

public abstract class AbstractJson {
	@Override
	public String toString() {
		return toPrettyJson();
	}

	public String toJson() {
		return JsonUtil.toJson(this);
	}

	public String toPrettyJson() {
		return JsonUtil.toPrettyJson(this);
	}
}
