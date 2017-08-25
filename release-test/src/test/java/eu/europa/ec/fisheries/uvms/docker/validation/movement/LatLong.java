package eu.europa.ec.fisheries.uvms.docker.validation.movement;

import java.util.Date;

public class LatLong {
	
	public double latitude;
	public double longitude;
	public Date positionTime;
	public LatLong(double latitude, double longitude, Date positionTime) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
		this.positionTime = positionTime;
	}
	@Override
	public String toString() {
		
		String formatStr = "%2.6f";
		String la = String.format(formatStr , latitude) ;
		String lo = String.format(formatStr , longitude) ;
		
		
		return "[lat=" + la + ", lon=" + lo + ", pos=" + positionTime + "]";
	}
	
	
	
	

}
