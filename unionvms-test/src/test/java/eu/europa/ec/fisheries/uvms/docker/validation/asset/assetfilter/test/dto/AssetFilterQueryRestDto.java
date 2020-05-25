package eu.europa.ec.fisheries.uvms.docker.validation.asset.assetfilter.test.dto;

import java.util.List;

public class AssetFilterQueryRestDto {
    private List<AssetFilterValueRestTestDto> values;
    private String type;
    private boolean inverse;
    private boolean isNumber;

    public List<AssetFilterValueRestTestDto> getValues() {
        return values;
    }

    public void setValues(List<AssetFilterValueRestTestDto> values) {
        this.values = values;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isInverse() {
        return inverse;
    }

    public void setInverse(boolean inverse) {
        this.inverse = inverse;
    }

    public boolean isNumber() {
        return isNumber;
    }

    public void setNumber(boolean isNumber) {
        this.isNumber = isNumber;
    }

}
