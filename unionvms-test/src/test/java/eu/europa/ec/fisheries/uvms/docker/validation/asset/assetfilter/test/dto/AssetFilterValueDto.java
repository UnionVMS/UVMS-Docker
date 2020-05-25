package eu.europa.ec.fisheries.uvms.docker.validation.asset.assetfilter.test.dto;

import java.io.Serializable;
import java.util.UUID;
public class AssetFilterValueDto implements Serializable{
	private static final long serialVersionUID = -9198706970175762738L;
	
	private UUID id;
    private Double valueNumber;
    private String valueString;
    private String operator;
    private AssetFilterQueryDto assetFilterQuery;

    public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}
	
	public Double getValueNumber() {
		return valueNumber;
	}

	public void setValueNumber(Double valueNumber) {
		this.valueNumber = valueNumber;
	}

	public String getValueString() {
		return valueString;
	}

	public void setValueString(String valueString) {
		this.valueString = valueString;
	}

	public String getOperator() {
		return operator;
	}
	
	public void setOperator(String operator) {
		this.operator = operator;
	}

	public AssetFilterQueryDto getAssetFilterQuery() {
		return assetFilterQuery;
	}

	public void setAssetFilterQuery(AssetFilterQueryDto assetFilterQuery) {
		this.assetFilterQuery = assetFilterQuery;
	}
}
