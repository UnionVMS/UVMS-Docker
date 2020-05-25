package eu.europa.ec.fisheries.uvms.docker.validation.asset.assetfilter.test.dto;

public class AssetFilterValueRestTestDto {
    private Double value;
    private String operator;

    public Double getValue() {
        return value;
    }

    public String getOperator() {
        return operator;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
}
