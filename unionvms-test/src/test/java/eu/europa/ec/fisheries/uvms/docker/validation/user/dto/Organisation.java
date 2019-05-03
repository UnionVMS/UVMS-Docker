package eu.europa.ec.fisheries.uvms.docker.validation.user.dto;

import java.io.Serializable;
import java.util.List;

/**
 * Holds details of an organisation.
 */
public class Organisation implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private String description;
    private String nation;
    private String status;
    private Long organisationId;
    private String parent;
    private List<EndPoint> endpoints;
    private String email;
    private int assignedUsers;

    /**
     * Creates a new instance.
     */
    public Organisation() {}

    /**
     * Get the value of organisation's description
     *
     * @return the value of organisation's description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the value of organisation's description
     *
     * @param description new value of organisation's description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get the value of nation to which the organisation belongs
     *
     * @return the value of nation
     */
    public String getNation() {
        return nation;
    }

    /**
     * Set the value of nation to which the organisation belongs
     *
     * @param nation new value of nation
     */
    public void setNation(String nation) {
        this.nation = nation;
    }

    /**
     * Get the value of organisation's status
     *
     * @return the value of status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Set the value of organisation's status
     *
     * @param status new value of status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Get the value of organisation's parent
     *
     * @return the value of organisation's parent
     */
    public String getParent() {
        return parent;
    }

    /**
     * Set the value of organisation's parent
     *
     * @param parent new value of organisation's parent
     */
    public void setParent(String parent) {
        this.parent = parent;
    }

    /**
     * Get the value of organisation's id
     *
     * @return the value of organisation's id
     */
    public Long getOrganisationId() {
        return organisationId;
    }

    /**
     * Set the value of organisation's id
     *
     * @param organisationId new value of organisation's id
     */
    public void setOrganisationId(Long organisationId) {
        this.organisationId = organisationId;
    }

    /**
     * @return the endpoints
     */
    public List<EndPoint> getEndpoints() {
        return endpoints;
    }

    /**
     * @param endpoints the endpoints to set
     */
    public void setEndpoints(List<EndPoint> endpoints) {
        this.endpoints = endpoints;
    }

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
     * @return the activeUsers
     */
    public int getAssignedUsers() {
        return assignedUsers;
    }

    /**
     * @param assignedUsers the activeUsers to set
     */
    public void setAssignedUsers(int assignedUsers) {
        this.assignedUsers = assignedUsers;
    }

    /**
     * Formats a human-readable view of this instance.
     *
     * @return a human-readable view
     */
    @Override
    public String toString() {
        return "Organisation{" + "name=" + name + ", description=" + description + ", nation=" + nation + ", status="
                + status + ", organisationId=" + organisationId + ", parent=" + parent + ", email=" + email
                + ", enpoints=" + endpoints + ", assignedUsers=" + assignedUsers + '}';
    }

}
