package eu.europa.ec.fisheries.uvms.docker.validation.user.dto;

import java.io.Serializable;

/**
 * Holds the details of a channel
 *
 */
public class Channel implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long channelId;
    private String dataflow;
    private String service;
    private Integer priority;
    private Long endpointId;

    /**
     * @return the channelId
     */
    public Long getChannelId() {
        return channelId;
    }

    /**
     * @param channelId the channelId to set
     */
    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    /**
     * @return the dataflow
     */
    public String getDataflow() {
        return dataflow;
    }

    /**
     * @param dataflow the dataflow to set
     */
    public void setDataflow(String dataflow) {
        this.dataflow = dataflow;
    }

    /**
     * @return the service
     */
    public String getService() {
        return service;
    }

    /**
     * @param service the service to set
     */
    public void setService(String service) {
        this.service = service;
    }

    /**
     * @return the priority
     */
    public Integer getPriority() {
        return priority;
    }

    /**
     * @param priority the priority to set
     */
    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    /**
     * @return the endpointId
     */
    public Long getEndpointId() {
        return endpointId;
    }

    /**
     * @param endpointId the endpointId to set
     */
    public void setEndpointId(Long endpointId) {
        this.endpointId = endpointId;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Channel [channelId=" + channelId + ", dataflow=" + dataflow + ", service=" + service + ", priority="
                + priority + ", endpointId=" + endpointId + "]";
    }

}
