package eu.europa.ec.fisheries.uvms.docker.validation.user.dto;

import java.io.Serializable;

/**
 * Holds a UserContext.<br/>
 */
public class UserContext implements Serializable {
    private static final long serialVersionUID = 1L;
    private String userName;
    private String applicationName;
    private ContextSet contextSet;

    /**
     * Creates a new instance
     */
    public UserContext() {}

    /**
     * Get the value of userName
     *
     * @return the value of userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Set the value of userName
     *
     * @param userName new value of userName
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Get the value of applicationName
     *
     * @return the value of applicationName
     */
    public String getApplicationName() {
        return applicationName;
    }

    /**
     * Set the value of applicationName
     *
     * @param applicationName new value of applicationName
     */
    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    /**
     * Get the value of contextSet
     *
     * @return the value of contextSet
     */
    public ContextSet getContextSet() {
        return contextSet;
    }

    /**
     * Set the value of contextSet
     *
     * @param contextSet new value of contextSet
     */
    public void setContextSet(ContextSet contextSet) {
        this.contextSet = contextSet;
    }

}
