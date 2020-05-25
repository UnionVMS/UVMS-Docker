package eu.europa.ec.fisheries.uvms.docker.validation.assetfilter.test.dto;

import java.util.List;

public class AssetFilterResponseDto {
	private String name;
	private String id;
	private List<AssetFilterQueryRestDto> filter;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public List<AssetFilterQueryRestDto> getFilter() {
		return filter;
	}
	public void setFilter(List<AssetFilterQueryRestDto> filter) {
		this.filter = filter;
	}
	
	

}
