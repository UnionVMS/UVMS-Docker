package eu.europa.ec.fisheries.uvms.docker.validation.user.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.Set;
import javax.json.bind.annotation.JsonbDateFormat;

/**
 * Holds a Scope based on a set of datasets from any applications. The goal is to limit the visibility on data.
 */
public class Scope implements Serializable {
    private static final long serialVersionUID = 1L;
    private String scopeName;
    @JsonbDateFormat("yyyy-MM-dd'T'HH:mm:ss[.SSS]X'['VV']'")
    private Instant activeFrom;
    @JsonbDateFormat("yyyy-MM-dd'T'HH:mm:ss[.SSS]X'['VV']'")
    private Instant activeTo;
    @JsonbDateFormat("yyyy-MM-dd'T'HH:mm:ss[.SSS]X'['VV']'")
    private Instant dataFrom;
    @JsonbDateFormat("yyyy-MM-dd'T'HH:mm:ss[.SSS]X'['VV']'")
    private Instant dataTo;
    private Set<DataSet> datasets;

    /**
     * Creates a new instance
     */
    public Scope() {}

    /**
     * Get the value of scopeName
     *
     * @return the value of scopeName
     */
    public String getScopeName() {
        return scopeName;
    }

    /**
     * Set the value of scopeName
     *
     * @param scopeName new value of scopeName
     */
    public void setScopeName(String scopeName) {
        this.scopeName = scopeName;
    }

    /**
     * Get the value of activeFrom
     *
     * @return the value of activeFrom
     */
    public Instant getActiveFrom() {
        return activeFrom;
    }

    /**
     * Set the value of activeFrom
     *
     * @param activeFrom new value of activeFrom
     */
    public void setActiveFrom(Instant activeFrom) {
        this.activeFrom = activeFrom;
    }

    /**
     * Get the value of activeTo
     *
     * @return the value of activeTo
     */
    public Instant getActiveTo() {
        return activeTo;
    }

    /**
     * Set the value of activeTo
     *
     * @param activeTo new value of activeTo
     */
    public void setActiveTo(Instant activeTo) {
        this.activeTo = activeTo;
    }

    /**
     * Get the value of dataFrom
     *
     * @return the value of dataFrom
     */
    public Instant getDataFrom() {
        return dataFrom;
    }

    /**
     * Set the value of dataFrom
     *
     * @param dataFrom new value of dataFrom
     */
    public void setDataFrom(Instant dataFrom) {
        this.dataFrom = dataFrom;
    }

    /**
     * Get the value of dataTo
     *
     * @return the value of dataTo
     */
    public Instant getDataTo() {
        return dataTo;
    }

    /**
     * Set the value of dataTo
     *
     * @param dataTo new value of dataTo
     */
    public void setDataTo(Instant dataTo) {
        this.dataTo = dataTo;
    }

    /**
     * Get the value of datasets
     *
     * @return the value of datasets
     */
    public Set<DataSet> getDatasets() {
        return datasets;
    }

    /**
     * Set the value of datasets
     *
     * @param datasets new value of datasets
     */
    public void setDatasets(Set<DataSet> datasets) {
        this.datasets = datasets;
    }

    /**
     * Formats a human-readable view of this instance.
     * 
     * @return a human-readable view
     */
    @Override
    public String toString() {
        return "Scope{" + "scopeName=" + scopeName + ", activeFrom=" + activeFrom + ", activeTo=" + activeTo
                + ", dataFrom=" + dataFrom + ", dataTo=" + dataTo + ", datasets=" + datasets + '}';
    }

}
