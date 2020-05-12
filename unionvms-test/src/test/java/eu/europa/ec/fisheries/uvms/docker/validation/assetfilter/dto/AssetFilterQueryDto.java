package eu.europa.ec.fisheries.uvms.docker.validation.assetfilter.dto;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

public class AssetFilterQueryDto implements Serializable {

	private static final long serialVersionUID = 4014705294222745778L;

	private UUID id;
    private String type;
    private boolean inverse;
    private boolean isNumber;
    private Set<AssetFilterValueDto> values;
    private AssetFilterDto assetFilter;
    
    
    public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean getInverse() {
		return inverse;
	}

	public void setInverse(boolean inverse) {
		this.inverse = inverse;
	}

	public boolean getIsNumber() {
		return isNumber;
	}

	public void setIsNumber(boolean isNumber) {
		this.isNumber = isNumber;
	}

	public Set<AssetFilterValueDto> getValues() {
		return values;
	}

	public void setValues(Set<AssetFilterValueDto> values) {
		this.values = values;
	}
	
	public AssetFilterDto getAssetFilter() {
		return assetFilter;
	}

	public void setAssetFilter(AssetFilterDto assetFilter) {
		this.assetFilter = assetFilter;
	}
}
