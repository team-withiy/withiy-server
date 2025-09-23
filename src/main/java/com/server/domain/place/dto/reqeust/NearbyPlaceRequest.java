package com.server.domain.place.dto.reqeust;

import lombok.Getter;

@Getter
public class NearbyPlaceRequest {

	private double latitude;
	private double longitude;
	private double radius;

}
