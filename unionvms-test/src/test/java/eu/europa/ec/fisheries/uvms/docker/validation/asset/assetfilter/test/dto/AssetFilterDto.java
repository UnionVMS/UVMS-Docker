package eu.europa.ec.fisheries.uvms.docker.validation.asset.assetfilter.test.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public class AssetFilterDto implements Serializable {
    private static final long serialVersionUID = 4679552553188147724L;

    private UUID id;
    private String name;
    private Instant updateTime;
    private String updatedBy;
    private String owner;
    private Set<AssetFilterQueryDto> queries;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Instant updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Set<AssetFilterQueryDto> getQueries() {
        return queries;
    }

    public void setQueries(Set<AssetFilterQueryDto> queries) {
        this.queries = queries;
    }
}
