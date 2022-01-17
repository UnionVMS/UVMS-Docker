package eu.europa.ec.fisheries.uvms.docker.validation.asset.assetfilter.test.dto;

import java.util.List;
import javax.json.JsonObject;

public class AssetFilterDto {
    private String name;
    private String id;
    private List<JsonObject> filter;

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

    public List<JsonObject> getFilter() {
        return filter;
    }

    public void setFilter(List<JsonObject> filter) {
        this.filter = filter;
    }

}
