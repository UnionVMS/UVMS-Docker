package eu.europa.ec.fisheries.uvms.docker.validation.streamcollector.dto;

import eu.europa.ec.fisheries.uvms.asset.remote.dto.search.SearchBranch;

import java.util.List;

public class ReportOneRequestDto {


    SearchBranch assetQuery;
    List<String> sources;

    int page = 1;
    int size = 100000;
    boolean dynamic = true;
    boolean includeInactivated = false;
    String startDate = "";
    String endDate = "";

    public SearchBranch getAssetQuery() {
        return assetQuery;
    }

    public void setAssetQuery(SearchBranch assetQuery) {
        this.assetQuery = assetQuery;
    }

    public List<String> getSources() {
        return sources;
    }

    public void setSources(List<String> sources) {
        this.sources = sources;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public boolean isDynamic() {
        return dynamic;
    }

    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic;
    }

    public boolean isIncludeInactivated() {
        return includeInactivated;
    }

    public void setIncludeInactivated(boolean includeInactivated) {
        this.includeInactivated = includeInactivated;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
