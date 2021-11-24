package eu.europa.ec.fisheries.uvms.docker.validation.user.dto;

import java.io.Serializable;
import java.util.List;

/**
 * Holds the detail of an end point
 *
 */
public class EndPoint implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String description;
    private String status;
    private String uri;
    private String email;
    private Long endpointId;
    private List<Channel> channelList;
    private String organisationName;
    private List<EndPointContact> persons;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the uRI
     */
    public String getUri() {
        return uri;
    }

    /**
     * @param uri the uRI to set
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
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

    /**
     * @return the channelList
     */
    public List<Channel> getChannelList() {
        return channelList;
    }

    /**
     * @param channelList the channelList to set
     */
    public void setChannelList(List<Channel> channelList) {
        this.channelList = channelList;
    }

    /**
     * @return the persons
     */
    public List<EndPointContact> getPersons() {
        return persons;
    }

    /**
     * @param persons the persons to set
     */
    public void setPersons(List<EndPointContact> persons) {
        this.persons = persons;
    }

    /**
     * @return the organisationName
     */
    public String getOrganisationName() {
        return organisationName;
    }

    /**
     * @param organisationName the organisationName to set
     */
    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "EndPoint [name=" + name + ", description=" + description + ", status=" + status + ", URI=" + uri
                + ", email=" + ", endpointId=" + endpointId + " , channelList=" + channelList + ", organisationName= "
                + organisationName + " , persons=" + persons + "]";
    }

}
