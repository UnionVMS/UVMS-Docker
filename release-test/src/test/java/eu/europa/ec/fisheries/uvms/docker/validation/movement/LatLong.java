package eu.europa.ec.fisheries.uvms.docker.validation.movement;

import java.util.Date;

public class LatLong {
	
	public double latitude;
	public double longitude;
	public Date positionTime;
	public LatLong(double longitude, double latitude, Date positionTime) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
		this.positionTime = positionTime;
	}

}
